package com.example.puzzlegame.ui.levelselection

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

    private val _scrollState = MutableStateFlow(ScrollState())
    val scrollState: StateFlow<ScrollState> = _scrollState.asStateFlow()

    private val _isPremiumPurchased = MutableStateFlow(false)
    val isPremiumPurchased: StateFlow<Boolean> = _isPremiumPurchased.asStateFlow()

    val purchaseResult: LiveData<BillingRepository.PurchaseResult> = billingRepository.getPurchaseResult()

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
            billingRepository.launchBillingFlow(activity)
        }
    }

    data class ScrollState(
        val index: Int = 0,
        val offset: Int = 0
    )
}
