package com.hakutogames.galaxycross.repository

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.hakutogames.galaxycross.local.db.BillingDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import javax.inject.Singleton

interface BillingRepository {
    val currentAccountIdFlow: StateFlow<String?>
    val purchaseResult: SharedFlow<PurchaseResult>
    fun isLoggedIn(): Boolean
    fun getPurchaseState(accountId: String): StateFlow<Boolean>
    fun setCurrentAccountId(id: String?)
    fun launchBillingFlow(activity: Activity)
    suspend fun isPremiumPurchased(accountId: String): Boolean

    sealed class PurchaseResult {
        data object Success : PurchaseResult()
        data object Canceled : PurchaseResult()
        data class Error(val message: String?) : PurchaseResult()
    }
}

@Singleton
class BillingRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val billingDataStore: BillingDataStore,
) : BillingRepository, PurchasesUpdatedListener {

    companion object {
        private const val PRODUCT_ID = "product_1"
        private const val TAG = "BillingRepositoryImpl"
    }

    private var billingClient: BillingClient? = null

    private val _currentAccountIdFlow = MutableStateFlow<String?>(null)
    override val currentAccountIdFlow: StateFlow<String?> get() = _currentAccountIdFlow

    override fun setCurrentAccountId(id: String?) {
        _currentAccountIdFlow.value = id
    }

    val currentAccountId: String?
        get() = _currentAccountIdFlow.value

    override fun isLoggedIn(): Boolean = (currentAccountId != null)

    // アカウントIDごとのフローを取得する場合は、Flowを引数で作り直すか、
    // データクラスやMapなどで管理するなど実装パターンは多数あります。
    // ここではシンプルに "最後に指定されたaccountId" のみに対応する実装例を示します:
    private val _currentAccountPurchaseState = MutableStateFlow(false)
    override fun getPurchaseState(accountId: String): StateFlow<Boolean> {
        // BillingDataStoreのFlowを購読して反映
        coroutineScope.launch {
            billingDataStore.isPremiumPurchasedForAccount(accountId).collect { isPurchased ->
                _currentAccountPurchaseState.emit(isPurchased)
            }
        }
        return _currentAccountPurchaseState.asStateFlow()
    }

    private val _purchaseResult = MutableSharedFlow<BillingRepository.PurchaseResult>()
    override val purchaseResult: SharedFlow<BillingRepository.PurchaseResult> = _purchaseResult.asSharedFlow()

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _isBillingClientReady = MutableStateFlow(false)
    private val isBillingClientReady: StateFlow<Boolean> = _isBillingClientReady.asStateFlow()

    fun initializeBillingClient() {
        if (billingClient?.isReady == true) return

        billingClient = BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases()
            .build()

        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    coroutineScope.launch {
                        _isBillingClientReady.emit(true)
                        queryPurchases()
                    }
                } else {
                    coroutineScope.launch {
                        _purchaseResult.emit(
                            BillingRepository.PurchaseResult.Error(
                                "Billing setup failed: ${billingResult.debugMessage}",
                            ),
                        )
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.d(TAG, "Billing service disconnected")
                coroutineScope.launch {
                    _isBillingClientReady.emit(false)
                    _purchaseResult.emit(
                        BillingRepository.PurchaseResult.Error("Billing service disconnected"),
                    )
                }
            }
        })
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?,
    ) {
        Log.d(TAG, "onPurchasesUpdated called.")

        // 既存の内容に加えて、responseCodeやdebugMessageをログに追加
        Log.d(TAG, "BillingResult: code=${billingResult.responseCode}, message=${billingResult.debugMessage}")

        // 詳細なPurchase情報をJSON形式で出してみる（可能な限り）
        purchases?.forEach { purchase ->
            Log.d(
                TAG,
                """
            Purchase Info:
            Purchase JSON: ${purchase.originalJson}
            Order ID: ${purchase.orderId}
            Purchase State: ${purchase.purchaseState}
            Products: ${purchase.products}
            Package Name: ${purchase.packageName}
            Purchase Token: ${purchase.purchaseToken}
            Acknowledged: ${purchase.isAcknowledged}
            AutoRenewing: ${purchase.isAutoRenewing} // サブスクの場合に有効
                """.trimIndent(),
            )
        }

        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            coroutineScope.launch {
                _purchaseResult.emit(BillingRepository.PurchaseResult.Canceled)
            }
        } else {
            coroutineScope.launch {
                _purchaseResult.emit(
                    BillingRepository.PurchaseResult.Error("Purchase failed: ${billingResult.debugMessage}"),
                )
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        val obfuscatedAccountId = purchase.accountIdentifiers?.obfuscatedAccountId
        if (!purchase.products.contains(PRODUCT_ID)) return

        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient?.acknowledgePurchase(acknowledgePurchaseParams) { br ->
                    if (br.responseCode == BillingClient.BillingResponseCode.OK) {
                        coroutineScope.launch {
                            // アカウントIDごとに保存
                            billingDataStore.setPremiumPurchasedForAccount(obfuscatedAccountId, true)
                            _purchaseResult.emit(BillingRepository.PurchaseResult.Success)
                        }
                    } else {
                        coroutineScope.launch {
                            billingDataStore.setPremiumPurchasedForAccount(obfuscatedAccountId, true)
                            _purchaseResult.emit(BillingRepository.PurchaseResult.Success)
                        }
                    }
                }
            } else {
                // ★ここで共通キー setPremiumPurchased(true) を呼ばない
                // 代わりにアカウントID付きで保存する
                coroutineScope.launch {
                    billingDataStore.setPremiumPurchasedForAccount(obfuscatedAccountId, true)
                    _purchaseResult.emit(BillingRepository.PurchaseResult.Success)
                }
            }
        } else {
            Log.d(TAG, "Purchase state is NOT PURCHASED: ${purchase.purchaseState}. Ignoring.")
        }
    }

    override suspend fun isPremiumPurchased(accountId: String): Boolean {
        return billingDataStore.isPremiumPurchasedForAccount(accountId).first()
    }

    fun queryPurchases() {
        billingClient?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
        ) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                var foundValidPurchase = false
                purchases.forEach { purchase ->
                    if (purchase.products.contains(PRODUCT_ID)) {
                        // 購入状態が PURCHASED なら handlePurchase
                        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                            handlePurchase(purchase)
                            foundValidPurchase = true
                        }
                    }
                }
            } else {
                coroutineScope.launch {
                    _purchaseResult.emit(
                        BillingRepository.PurchaseResult.Error(
                            "Query purchases failed: ${billingResult.debugMessage}",
                        ),
                    )
                }
            }
        }
    }

    override fun launchBillingFlow(activity: Activity) {
        coroutineScope.launch {
            val accountId = currentAccountId
            if (accountId.isNullOrEmpty()) {
                // ログインしていない場合のエラー処理
                _purchaseResult.emit(
                    BillingRepository.PurchaseResult.Error("Not logged in; cannot launchBillingFlow."),
                )
                return@launch
            }

            // 以下は、既存の処理をそのまま流用（例） -------------------------
            if (isPremiumPurchased(accountId)) {
                _purchaseResult.emit(
                    BillingRepository.PurchaseResult.Error("Premium already purchased."),
                )
                return@launch
            }

            // BillingClient初期化待ちなどはそのまま
            if (!isBillingClientReady.value) {
                initializeBillingClient()
                try {
                    withTimeout(5000) {
                        isBillingClientReady.filter { it }.first()
                    }
                } catch (e: TimeoutCancellationException) {
                    _purchaseResult.emit(
                        BillingRepository.PurchaseResult.Error("BillingClient init timed out."),
                    )
                    return@launch
                }
            }

            // ProductDetailsを取得
            val productList = listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(PRODUCT_ID)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build(),
            )
            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build()

            billingClient?.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK &&
                    productDetailsList.isNotEmpty()
                ) {
                    val productDetails = productDetailsList[0]
                    val productDetailsParamsList = listOf(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(productDetails)
                            .build(),
                    )

                    // ここで setObfuscatedAccountId に currentAccountId を設定
                    val billingFlowParams = BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(productDetailsParamsList)
                        .setObfuscatedAccountId(accountId)
                        .build()

                    billingClient?.launchBillingFlow(activity, billingFlowParams)
                } else {
                    coroutineScope.launch {
                        _purchaseResult.emit(
                            BillingRepository.PurchaseResult.Error(
                                "Failed to retrieve product details: ${billingResult.debugMessage}",
                            ),
                        )
                    }
                }
            }
        }
    }
}
