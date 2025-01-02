package com.example.puzzlegame.ui.home

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.puzzlegame.repository.BillingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LevelSelectionViewModel @Inject constructor(
    private val billingRepository: BillingRepository,
) : ViewModel() {

    // State to hold scroll information
    private val _scrollState = MutableStateFlow(ScrollState())
    val scrollState: StateFlow<ScrollState> = _scrollState.asStateFlow()

    // State to hold premium purchase status
    private val _isPremiumPurchased = MutableStateFlow(false)
    val isPremiumPurchased: StateFlow<Boolean> = _isPremiumPurchased.asStateFlow()

    // Expose purchase result from BillingRepository
    val purchaseResult: LiveData<BillingRepository.PurchaseResult> = billingRepository.purchaseResult

    init {
        viewModelScope.launch {
            // Observe purchase state from BillingRepository
            billingRepository.purchaseState.collect { isPurchased ->
                _isPremiumPurchased.value = isPurchased
            }
        }
    }

    /**
     * Initiates the purchase flow for the premium product.
     * The Activity context must be provided to launch the billing flow.
     *
     * @param activity The current Activity context.
     */
    fun startPremiumPurchase(activity: Activity) {
        viewModelScope.launch {
            billingRepository.launchBillingFlow(activity)
        }
    }

    /**
     * Optionally, check premium status from a remote source like Firestore.
     * This can be combined with local purchase state for enhanced reliability.
     */
    private suspend fun checkPremiumStatus() {
        try {
            // Example: Fetch premium status from Firestore or another backend service
            // val isPremium = firestoreRepository.isUserPremium(userId)
            // _isPremiumPurchased.value = isPremium

            // For demonstration, we'll rely on local purchase state
            _isPremiumPurchased.value = billingRepository.isPremiumPurchased()
        } catch (e: Exception) {
            // Handle exceptions, possibly by logging or updating UI state
        }
    }

    // Function to update scroll state
    fun updateScrollState(index: Int, offset: Int) {
        _scrollState.value = ScrollState(index, offset)
    }

    // Data class to represent scroll state
    data class ScrollState(
        val index: Int = 0,
        val offset: Int = 0
    )
}
