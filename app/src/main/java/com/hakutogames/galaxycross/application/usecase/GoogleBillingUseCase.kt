package com.hakutogames.galaxycross.application.usecase

import android.app.Activity
import com.hakutogames.galaxycross.repository.BillingRepository
import com.hakutogames.galaxycross.repository.BillingRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

interface GoogleBillingUseCase {
    val isPremiumPurchased: Flow<Boolean>
    val purchaseResult: SharedFlow<BillingRepository.PurchaseResult>
    fun queryPurchases()
    suspend fun launchBillingFlow(activity: Activity)
    suspend fun clearPurchaseByToken(purchaseToken: String)
}

class GoogleBillingUseCaseImpl @Inject constructor(
    private val billingRepository: BillingRepositoryImpl,
) : GoogleBillingUseCase {

    override val isPremiumPurchased: Flow<Boolean>
        get() = billingRepository.isPremiumPurchased

    override val purchaseResult: SharedFlow<BillingRepository.PurchaseResult>
        get() = billingRepository.purchaseResult

    override suspend fun launchBillingFlow(activity: Activity) {
        billingRepository.launchBillingFlow(activity)
    }

    override suspend fun clearPurchaseByToken(purchaseToken: String) {
        billingRepository.clearPurchaseByToken(purchaseToken)
    }

    override fun queryPurchases() {
        billingRepository.queryPurchases()
    }
}
