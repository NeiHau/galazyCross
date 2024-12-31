package com.example.puzzlegame.repository

import com.example.puzzlegame.domain.PurchaseData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface FirebaseRepository {
    suspend fun verifyPurchase(purchaseToken: String, productId: String): Boolean
    suspend fun savePurchaseData(purchaseData: PurchaseData)
}

class FirebaseRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val functions: FirebaseFunctions,
) : FirebaseRepository {

    override suspend fun verifyPurchase(purchaseToken: String, productId: String): Boolean {
        return try {
            // Cloud Functionsを呼び出して購入を検証するためのデータを準備
            val data = hashMapOf(
                "purchaseToken" to purchaseToken,
                "productId" to productId
            )

            // Cloud Functionsを呼び出し、結果を取得
            val result = functions
                .getHttpsCallable("verifyPurchase")
                .call(data)
                .await()
                .getData() as? Map<*, *>  // データを取得してMapにキャスト

            // 結果からsuccessフラグを取得
            result?.get("success") as? Boolean ?: false
        } catch (e: Exception) {
            // エラーが発生した場合はfalseを返す
            false
        }
    }

    override suspend fun savePurchaseData(purchaseData: PurchaseData) {
        withContext(Dispatchers.IO) {
            // データクラスをマップに変換する際も型安全性を保持
            val firestoreData = mapOf(
                "purchaseToken" to purchaseData.purchaseToken,
                "orderId" to purchaseData.orderId,
                "productId" to purchaseData.productId,
                "purchaseTime" to purchaseData.purchaseTime,
                "deviceId" to purchaseData.deviceId,
                "status" to purchaseData.status,
                "acknowledgementState" to purchaseData.acknowledgementState
            )

            firestore.collection("purchases")
                .document(purchaseData.purchaseToken)
                .set(firestoreData)
                .await()
        }
    }
}