package com.hakutogames.galaxycross.ui.levelselection

import android.app.Activity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hakutogames.galaxycross.repository.BillingRepository
import com.hakutogames.galaxycross.repository.BillingRepositoryImpl
import com.hakutogames.galaxycross.ui.ext.asEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LevelSelectionViewModel @Inject constructor(
    private val billingRepository: BillingRepositoryImpl,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val isPremiumPurchased: StateFlow<Boolean> = billingRepository.isPremiumPurchased

    private val _scrollToLevelIndex = savedStateHandle.getStateFlow<Int?>("scroll_index", null)
    val scrollToLevelIndex: StateFlow<Int?> = _scrollToLevelIndex

    private val _uiEvent = Channel<UiEvent>(Channel.CONFLATED)
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asEvent()

    private var isPurchaseInProgress = false

    init {
        // clearPurchaseByToken("")
        viewModelScope.launch {
            billingRepository.purchaseResult.collect { result ->
                if (!isPurchaseInProgress) return@collect

                when (result) {
                    is BillingRepository.PurchaseResult.Success -> {
                        _uiEvent.send(UiEvent.PurchaseSuccess)
                        isPurchaseInProgress = false // 購入プロセスの終了
                    }
                    is BillingRepository.PurchaseResult.Canceled -> {
                        isPurchaseInProgress = false // 購入プロセスの終了
                    }
                    is BillingRepository.PurchaseResult.Error -> {
                        _uiEvent.send(UiEvent.PurchaseError(result.message))
                        isPurchaseInProgress = false // 購入プロセスの終了
                    }
                }
            }
        }
    }

    // 課金フローの開始
    fun startPremiumPurchase(activity: Activity) {
        isPurchaseInProgress = true
        billingRepository.launchBillingFlow(activity)
    }

    fun setScrollToLevelIndex(index: Int) {
        savedStateHandle["scroll_index"] = index
    }

    fun clearScrollToLevelIndex() {
        savedStateHandle["scroll_index"] = null
    }

    private fun clearPurchaseByToken(purchaseToken: String) {
        viewModelScope.launch {
            billingRepository.clearPurchaseByToken(purchaseToken)
        }
    }

    sealed class UiEvent {
        data object PurchaseSuccess : UiEvent()
        data class PurchaseError(val message: String?) : UiEvent()
    }
}
