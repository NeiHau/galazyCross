package com.hakutogames.galaxycross.repository

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
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
        object Success : PurchaseResult()
        object Canceled : PurchaseResult()
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

    private fun initializeBillingClient() {
        if (billingClient?.isReady == true) return

        billingClient = BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases()
            .build()

        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                Log.d(
                    TAG,
                    "onBillingSetupFinished: responseCode=${billingResult.responseCode}, " +
                        "debugMessage=${billingResult.debugMessage}",
                )
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
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()

                billingClient?.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        coroutineScope.launch {
                            purchase.orderId?.let {
                                billingDataStore.savePurchaseInfo(
                                    purchaseToken = purchase.purchaseToken,
                                    purchaseTime = purchase.purchaseTime,
                                    orderId = it,
                                )
                            }
                            billingDataStore.setPremiumPurchased(true) // Ensure this is called
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

    private fun queryPurchases() {
        billingClient?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
        ) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                for (purchase in purchases) {
                    handlePurchase(purchase)
                }
            } else {
                coroutineScope.launch {
                    _purchaseResult.emit(
                        BillingRepository.PurchaseResult.Error("Query purchases failed: ${billingResult.debugMessage}"),
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
                        Toast.makeText(context, "Failed to retrieve product details", Toast.LENGTH_SHORT).show()
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
