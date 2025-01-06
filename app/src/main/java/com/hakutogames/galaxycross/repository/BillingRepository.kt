package com.hakutogames.galaxycross.repository

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
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
    val purchaseResult: SharedFlow<PurchaseResult>
    val isPremiumPurchased: StateFlow<Boolean>
    fun launchBillingFlow(activity: Activity)

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

    // デバイス全体での購入状態を管理
    private val _isPremiumPurchased = MutableStateFlow(false)
    override val isPremiumPurchased: StateFlow<Boolean> = _isPremiumPurchased

    private val _purchaseResult = MutableSharedFlow<BillingRepository.PurchaseResult>()
    override val purchaseResult: SharedFlow<BillingRepository.PurchaseResult> = _purchaseResult.asSharedFlow()

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _isBillingClientReady = MutableStateFlow(false)
    private val isBillingClientReady: StateFlow<Boolean> = _isBillingClientReady.asStateFlow()

    init {
        // 起動時に購入状態を確認
        coroutineScope.launch {
            billingDataStore.isPremiumPurchased.collect { isPurchased ->
                _isPremiumPurchased.value = isPurchased
            }
        }
        initializeBillingClient()
    }

    fun initializeBillingClient(forceReinitialize: Boolean = false) {
        if (billingClient?.isReady == true && !forceReinitialize) {
            Log.d(TAG, "BillingClient is already ready.")
            return
        }

        // 既存の接続を完全に終了
        billingClient?.endConnection()
        billingClient = null

        // 新しいインスタンスを作成
        billingClient = BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases()
            .build()

        // 接続を開始
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    coroutineScope.launch {
                        _isBillingClientReady.emit(true)
                        // forceReinitialize時は購入状態の確認をスキップ
                        if (!forceReinitialize) {
                            queryPurchases()
                        }
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
                coroutineScope.launch {
                    _isBillingClientReady.emit(false)
                }
                if (!forceReinitialize) {
                    initializeBillingClient()
                }
            }
        })
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?,
    ) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                if (purchases != null) {
                    for (purchase in purchases) {
                        handlePurchase(purchase)
                    }
                }
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                // 既に購入済みの場合は購入状態を更新
                coroutineScope.launch {
                    billingDataStore.setPremiumPurchased(true)
                    _isPremiumPurchased.value = true
                    _purchaseResult.emit(BillingRepository.PurchaseResult.Success)
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                coroutineScope.launch {
                    _purchaseResult.emit(BillingRepository.PurchaseResult.Canceled)
                }
            }
            else -> {
                coroutineScope.launch {
                    _purchaseResult.emit(
                        BillingRepository.PurchaseResult.Error(
                            "Purchase failed: ${billingResult.debugMessage}",
                        ),
                    )
                }
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (!purchase.products.contains(PRODUCT_ID)) return

        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                acknowledgePurchase(purchase)
            } else {
                updatePurchaseState()
            }
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        billingClient?.acknowledgePurchase(acknowledgePurchaseParams) { br ->
            if (br.responseCode == BillingClient.BillingResponseCode.OK) {
                updatePurchaseState()
            }
        }
    }

    private fun updatePurchaseState() {
        coroutineScope.launch {
            billingDataStore.setPremiumPurchased(true)
            _isPremiumPurchased.value = true
            _purchaseResult.emit(BillingRepository.PurchaseResult.Success)
        }
    }

    fun queryPurchases() {
        billingClient?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
        ) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val hasValidPurchase = purchases.any { purchase ->
                    purchase.products.contains(PRODUCT_ID) &&
                        purchase.purchaseState == Purchase.PurchaseState.PURCHASED
                }

                coroutineScope.launch {
                    billingDataStore.setPremiumPurchased(hasValidPurchase)
                    _isPremiumPurchased.value = hasValidPurchase
                }
            }
        }
    }

    override fun launchBillingFlow(activity: Activity) {
        coroutineScope.launch {
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

                    val billingFlowParams = BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(productDetailsParamsList)
                        .build()

                    billingClient?.launchBillingFlow(activity, billingFlowParams)
                }
            }
        }
    }

    suspend fun clearPurchaseByToken(purchaseToken: String) {
        if (!isBillingClientReady.value) {
            try {
                withTimeout(5000) {
                    initializeBillingClient()
                    isBillingClientReady.filter { it }.first()
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "BillingClient initialization timed out")
                return
            }
        }

        try {
            // 指定されたpurchaseTokenの購入を消費
            val consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchaseToken)
                .build()

            billingClient?.consumeAsync(consumeParams) { consumeResult, _ ->
                when (consumeResult.responseCode) {
                    BillingClient.BillingResponseCode.OK -> {
                        // 消費成功後の処理
                        coroutineScope.launch {
                            billingDataStore.setPremiumPurchased(false)
                            _isPremiumPurchased.value = false
                            _purchaseResult.emit(BillingRepository.PurchaseResult.Success)

                            // BillingClientを再初期化して、キャッシュをクリア
                            initializeBillingClient(forceReinitialize = true)
                        }
                    }
                    BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> {
                        // 購入が見つからない場合も状態をクリア
                        coroutineScope.launch {
                            billingDataStore.setPremiumPurchased(false)
                            _isPremiumPurchased.value = false
                            _purchaseResult.emit(BillingRepository.PurchaseResult.Success)
                        }
                    }
                    else -> {
                        Log.e(TAG, "Failed to consume purchase: ${consumeResult.debugMessage}")
                        coroutineScope.launch {
                            _purchaseResult.emit(
                                BillingRepository.PurchaseResult.Error(
                                    "Failed to consume purchase: ${consumeResult.debugMessage}",
                                ),
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing purchase", e)
            coroutineScope.launch {
                billingDataStore.setPremiumPurchased(false)
                _isPremiumPurchased.value = false
                _purchaseResult.emit(
                    BillingRepository.PurchaseResult.Error(
                        "Error clearing purchase: ${e.message}",
                    ),
                )
            }
        }
    }

    /**
     * 購入状態を強制的に更新します。
     * システムレベルでの購入状態を確認し、アプリの状態と同期させます。
     */
    private fun forceRefreshPurchaseState() {
        billingClient?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
        ) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val hasValidPurchase = purchases.any { purchase ->
                    purchase.products.contains(PRODUCT_ID) &&
                        purchase.purchaseState == Purchase.PurchaseState.PURCHASED &&
                        purchase.isAcknowledged
                }

                coroutineScope.launch {
                    // DataStoreとメモリ上の状態を同期
                    billingDataStore.setPremiumPurchased(hasValidPurchase)
                    _isPremiumPurchased.value = hasValidPurchase
                }
            } else {
                // クエリが失敗した場合は、安全のため非購入状態とする
                coroutineScope.launch {
                    billingDataStore.setPremiumPurchased(false)
                    _isPremiumPurchased.value = false
                }
            }
        }
    }
}
