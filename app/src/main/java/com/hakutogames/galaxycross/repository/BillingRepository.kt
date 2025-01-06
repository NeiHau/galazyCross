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
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import javax.inject.Singleton

interface BillingRepository {
    fun getPurchaseState(): StateFlow<Boolean>
    val purchaseResult: SharedFlow<PurchaseResult>
    fun launchBillingFlow(activity: Activity)
    suspend fun isPremiumPurchased(): Boolean

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

    private val _purchaseState = MutableStateFlow(false)
    override fun getPurchaseState(): StateFlow<Boolean> = _purchaseState.asStateFlow()

    private val _purchaseResult = MutableSharedFlow<BillingRepository.PurchaseResult>()
    override val purchaseResult: SharedFlow<BillingRepository.PurchaseResult> = _purchaseResult.asSharedFlow()

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _isBillingClientReady = MutableStateFlow(false)
    private val isBillingClientReady: StateFlow<Boolean> = _isBillingClientReady.asStateFlow()

    init {
        coroutineScope.launch {
            billingDataStore.isPremiumPurchased.collect { isPurchased ->
                _purchaseState.emit(isPurchased)
            }
        }
    }

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
        Log.d(
            TAG,
            """
            Purchase Update Details:
            Response Code: ${billingResult.responseCode}
            Debug Message: ${billingResult.debugMessage}
            Response Code Name: ${getBillingResponseCodeName(billingResult.responseCode)}
            Purchases Size: ${purchases?.size}
            Purchase Details: ${purchases?.joinToString { purchase ->
                "OrderId: ${purchase.orderId}, State: ${purchase.purchaseState}"
            }}
            """.trimIndent(),
        )

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

    private fun getBillingResponseCodeName(responseCode: Int): String {
        return when (responseCode) {
            BillingClient.BillingResponseCode.OK -> "OK"
            BillingClient.BillingResponseCode.USER_CANCELED -> "USER_CANCELED"
            BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE -> "SERVICE_UNAVAILABLE"
            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> "BILLING_UNAVAILABLE"
            BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> "ITEM_UNAVAILABLE"
            BillingClient.BillingResponseCode.DEVELOPER_ERROR -> "DEVELOPER_ERROR"
            BillingClient.BillingResponseCode.ERROR -> "ERROR"
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> "ITEM_ALREADY_OWNED"
            BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> "ITEM_NOT_OWNED"
            else -> "UNKNOWN"
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        // 対象の商品が含まれているかどうか (単一商品ならこういう判定)
        if (!purchase.products.contains(PRODUCT_ID)) return

        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            // ここから先は「支払い済み」の処理
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()

                billingClient?.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        coroutineScope.launch {
                            // ここで isPremiumPurchased を true にする
                            billingDataStore.setPremiumPurchased(true)
                            _purchaseResult.emit(BillingRepository.PurchaseResult.Success)
                        }
                    } else {
                        coroutineScope.launch {
                            _purchaseResult.emit(
                                BillingRepository.PurchaseResult.Error(
                                    "Acknowledgment failed: ${billingResult.debugMessage}",
                                ),
                            )
                        }
                    }
                }
            } else {
                // すでに承認済みの場合
                coroutineScope.launch {
                    billingDataStore.setPremiumPurchased(true)
                    _purchaseResult.emit(BillingRepository.PurchaseResult.Success)
                }
            }
        }
    }

    override suspend fun isPremiumPurchased(): Boolean {
        return billingDataStore.isPremiumPurchased.first()
    }

    fun queryPurchases() {
        billingClient?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
        ) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                // ここで「該当する課金アイテムが購入済みかどうか」をチェック
                // （複数商品を扱う場合はループして対象アイテムを探す）
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

                // 「有効な購入が見つからなかった」なら購入フラグをfalseに更新
                if (!foundValidPurchase) {
                    coroutineScope.launch {
                        billingDataStore.setPremiumPurchased(false)
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
            if (isPremiumPurchased()) {
                _purchaseResult.emit(
                    BillingRepository.PurchaseResult.Error("Premium already purchased."),
                )
                return@launch
            }

            Log.d(TAG, "Starting launchBillingFlow, BillingClient ready: ${isBillingClientReady.value}")
            if (!isBillingClientReady.value) {
                initializeBillingClient()
                try {
                    withTimeout(5000) {
                        isBillingClientReady.filter { it }.first()
                    }
                } catch (e: TimeoutCancellationException) {
                    Log.e(TAG, "BillingClient initialization timed out")
                    _purchaseResult.emit(
                        BillingRepository.PurchaseResult.Error("BillingClient initialization timed out"),
                    )
                    return@launch
                }
            }

            val productList = listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(PRODUCT_ID)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build(),
            )
            Log.d(TAG, "Querying product details for productId: $PRODUCT_ID")

            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build()

            withContext(Dispatchers.Main) {
                billingClient?.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
                    Log.d(
                        TAG,
                        "queryProductDetailsAsync result: " +
                            "responseCode=${billingResult.responseCode}, " +
                            "debugMessage=${billingResult.debugMessage}, " +
                            "productDetails size=${productDetailsList.size}",
                    )

                    if (productDetailsList.isNotEmpty()) {
                        Log.d(
                            TAG,
                            "Product details found: " +
                                "productId=${productDetailsList[0].productId}, " +
                                "name=${productDetailsList[0].name}, " +
                                "type=${productDetailsList[0].productType}",
                        )
                    }

                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK &&
                        productDetailsList.isNotEmpty()
                    ) {
                        val productDetails = productDetailsList[0]
                        val productDetailsParamsList = listOf(
                            BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
                                .build(),
                        )

                        val billingFlowParams = BillingFlowParams.newBuilder()
                            .setProductDetailsParamsList(productDetailsParamsList)
                            .build()

                        val launchResult = billingClient?.launchBillingFlow(activity, billingFlowParams)
                        Log.d(
                            TAG,
                            "launchBillingFlow result: " +
                                "responseCode=${launchResult?.responseCode}, " +
                                "debugMessage=${launchResult?.debugMessage}",
                        )
                    } else {
                        Log.e(
                            TAG,
                            "Failed to retrieve product details: " +
                                "responseCode=${billingResult.responseCode}, " +
                                "debugMessage=${billingResult.debugMessage}",
                        )
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
}
