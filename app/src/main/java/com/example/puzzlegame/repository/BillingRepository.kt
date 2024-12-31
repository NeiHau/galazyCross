package com.example.puzzlegame.repository

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.provider.Settings
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.queryProductDetails
import com.example.puzzlegame.domain.PurchaseData
import com.google.firebase.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.sql.Date
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

// 課金処理を抽象化するインターフェース
interface BillingRepository {
    // 課金の状態を監視するためのFlow
    val purchaseResult: StateFlow<BillingRepositoryImpl.PurchaseResult>

    // 購入処理を開始する
    suspend fun startPurchase(productId: String)

    // リソースのクリーンアップ
    fun cleanup()
}

class BillingRepositoryImpl @Inject constructor(
    private val context: Context,
    private val firestore: FirebaseRepository,
):BillingRepository {
    // コルーチンスコープの設定
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    private val _purchaseResult = MutableStateFlow<PurchaseResult>(PurchaseResult.NotInitialized)
    override val purchaseResult: StateFlow<PurchaseResult> = _purchaseResult.asStateFlow()

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                if (purchases != null) {
                    scope.launch {
                        processPurchases(purchases)
                    }
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                _purchaseResult.value = PurchaseResult.Canceled
            }
            else -> {
                _purchaseResult.value = PurchaseResult.Error(billingResult.debugMessage)
            }
        }
    }

    // BillingClientの初期化は購入更新リスナーの後で行う
    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases()
        .build()

    // BillingClientの接続確認と接続
    private suspend fun ensureConnected() {
        if (!billingClient.isReady) {
            try {
                billingClient.startConnection(object : BillingClientStateListener {
                    override fun onBillingSetupFinished(billingResult: BillingResult) {
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            // 接続成功
                        } else {
                            _purchaseResult.value = PurchaseResult.Error("Billing setup failed")
                        }
                    }

                    override fun onBillingServiceDisconnected() {
                        // 再接続のロジック
                        scope.launch {
                            ensureConnected()
                        }
                    }
                })
            } catch (e: Exception) {
                _purchaseResult.value = PurchaseResult.Error("Connection failed: ${e.message}")
            }
        }
    }

    // 商品詳細の取得
    private suspend fun queryProductDetails(productId: String): ProductDetails? {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                )
            )
            .build()

        val result = billingClient.queryProductDetails(params)
        return result.productDetailsList?.firstOrNull()
    }

    // Firebaseで購入を検証
    private suspend fun verifyPurchaseWithFirebase(purchase: Purchase): Boolean {
        return firestore.verifyPurchase(
            purchaseToken = purchase.purchaseToken,
            productId = purchase.products[0]
        )
    }

    @SuppressLint("HardwareIds")
    private suspend fun savePurchaseToFirestore(purchase: Purchase) {
        val purchaseData = purchase.orderId?.let {
            PurchaseData(
                purchaseToken = purchase.purchaseToken,
                orderId = it,
                productId = purchase.products.firstOrNull().orEmpty(),
                purchaseTime = Timestamp(Date(purchase.purchaseTime)),
                deviceId = Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.ANDROID_ID
                ),
                status = purchase.purchaseState.toString(),
                acknowledgementState = purchase.isAcknowledged
            )
        }

        if (purchaseData != null) {
            firestore.savePurchaseData(purchaseData)
        }
    }

    // 購入の承認
    private suspend fun acknowledgePurchase(purchaseToken: String) {
        // suspendで実行するために、suspendCoroutineを使用
        return suspendCoroutine { continuation ->
            val params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchaseToken)
                .build()

            // acknowledgePurchaseにコールバックを渡す
            billingClient.acknowledgePurchase(params) { billingResult ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // 成功時は成功結果を返す
                    continuation.resume(Unit)
                } else {
                    // エラー時は例外を投げる
                    continuation.resumeWithException(
                        Exception("Failed to acknowledge purchase: ${billingResult.debugMessage}")
                    )
                }
            }
        }
    }

    // 購入の処理
    private suspend fun processPurchases(purchases: List<Purchase>) {
        for (purchase in purchases) {
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                val isValid = verifyPurchaseWithFirebase(purchase)

                if (isValid) {
                    savePurchaseToFirestore(purchase)

                    if (!purchase.isAcknowledged) {
                        acknowledgePurchase(purchase.purchaseToken)
                    }

                    _purchaseResult.value = PurchaseResult.Success
                }
            }
        }
    }

    // 購入フローを開始する公開メソッド
    override suspend fun startPurchase(productId: String) {
        ensureConnected()

        val productDetails = queryProductDetails(productId)
        if (productDetails == null) {
            _purchaseResult.value = PurchaseResult.Error("Product not found")
            return
        }

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        val activity = context as Activity
        billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    // リソースの解放
    override fun cleanup() {
        billingClient.endConnection()
        scope.cancel()
    }

    // 購入結果を表すシールドクラス
    sealed class PurchaseResult {
        data object NotInitialized : PurchaseResult()
        data object Success : PurchaseResult()
        data object Canceled : PurchaseResult()
        data class Error(val message: String) : PurchaseResult()
    }
}