package com.hakutogames.galaxycross.ui.levelselection

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hakutogames.galaxycross.repository.BillingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LevelSelectionViewModel @Inject constructor(
    private val billingRepository: BillingRepository,
) : ViewModel() {

    private val _isPremiumPurchased = MutableStateFlow(false)
    val isPremiumPurchased: StateFlow<Boolean> = _isPremiumPurchased.asStateFlow()

    // Remove LiveData and use SharedFlow
    val purchaseResult = billingRepository.purchaseResult

    init {
        viewModelScope.launch {
            billingRepository.getPurchaseState().collect { isPurchased ->
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
            if (!billingRepository.isPremiumPurchased()) {
                billingRepository.launchBillingFlow(activity)
            } else {
                // Optionally, notify that premium is already purchased
                // You can emit a specific event if needed
            }
        }
    }
}

