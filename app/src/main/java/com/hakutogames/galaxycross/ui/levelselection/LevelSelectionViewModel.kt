package com.hakutogames.galaxycross.ui.levelselection

import android.app.Activity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hakutogames.galaxycross.application.usecase.GoogleBillingUseCase
import com.hakutogames.galaxycross.domain.PurchaseResult
import com.hakutogames.galaxycross.ui.ext.asEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LevelSelectionViewModel @Inject constructor(
    private val billingUseCases: GoogleBillingUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val isPremiumPurchased: StateFlow<Boolean> = billingUseCases.isPremiumPurchased
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    private val _scrollToLevelIndex = savedStateHandle.getStateFlow<Int?>("scroll_index", null)
    val scrollToLevelIndex: StateFlow<Int?> = _scrollToLevelIndex

    private val _uiEvent = Channel<UiEvent>(Channel.CONFLATED)
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asEvent()

    private var isPurchaseInProgress = false

    init {
        clearPurchaseByToken(
            "ehlbcchnjnfggepfcbhcccom.AO-J1OweLh-9zKkFIN6goJnyr-" +
                "rhYZeWj8sjy1Hf5bIybDlyKPuV2cgtpmlWA83y2OPmb0RHAe4QMav" +
                "YiguudHwsVXv1OyXHatAO5fDHO5TmK7GPLupCN7s",
        )

        viewModelScope.launch {
            billingUseCases.purchaseResult.collect { result ->
                if (!isPurchaseInProgress) return@collect

                when (result) {
                    is PurchaseResult.Success -> {
                        _uiEvent.send(UiEvent.PurchaseSuccess)
                        isPurchaseInProgress = false
                    }
                    is PurchaseResult.Canceled -> {
                        _uiEvent.send(UiEvent.PurchaseCancel)
                        isPurchaseInProgress = false
                    }
                    is PurchaseResult.Error -> {
                        _uiEvent.send(UiEvent.PurchaseError(result.message))
                        isPurchaseInProgress = false
                    }
                }
            }
        }
    }

    fun startPremiumPurchase(activity: Activity) {
        viewModelScope.launch {
            isPurchaseInProgress = true
            billingUseCases.launchBillingFlow(activity)
        }
    }

    fun setScrollToLevelIndex(index: Int) {
        savedStateHandle["scroll_index"] = index
    }

    fun clearScrollToLevelIndex() {
        savedStateHandle["scroll_index"] = null
    }

    private fun clearPurchaseByToken(purchaseToken: String) {
        viewModelScope.launch {
            billingUseCases.clearPurchaseByToken(purchaseToken)
        }
    }

    sealed class UiEvent {
        data object PurchaseSuccess : UiEvent()
        data object PurchaseCancel : UiEvent()
        data class PurchaseError(val message: String?) : UiEvent()
    }
}
