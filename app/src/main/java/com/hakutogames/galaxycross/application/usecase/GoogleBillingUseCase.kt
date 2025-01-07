package com.hakutogames.galaxycross.application.usecase

import android.app.Activity
import com.hakutogames.galaxycross.domain.PurchaseResult
import com.hakutogames.galaxycross.repository.BillingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

interface GoogleBillingUseCase {
    val isPremiumPurchased: Flow<Boolean>
    val purchaseResult: SharedFlow<PurchaseResult>
    fun queryPurchases()
    suspend fun launchBillingFlow(activity: Activity)
    suspend fun clearPurchaseByToken(purchaseToken: String)
}

class GoogleBillingUseCaseImpl @Inject constructor(
    private val billingRepository: BillingRepository,
) : GoogleBillingUseCase {

    override val isPremiumPurchased: Flow<Boolean>
        get() = billingRepository.isPremiumPurchased

    override val purchaseResult: SharedFlow<PurchaseResult>
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
