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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LevelSelectionViewModel @Inject constructor(
    private val billingRepository: BillingRepositoryImpl,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _isPremiumPurchased = MutableStateFlow(false)
    val isPremiumPurchased: StateFlow<Boolean> = _isPremiumPurchased.asStateFlow()

    private val _scrollToLevelIndex = savedStateHandle.getStateFlow<Int?>("scroll_index", null)
    val scrollToLevelIndex: StateFlow<Int?> = _scrollToLevelIndex

    private val _uiEvent = Channel<UiEvent>(Channel.CONFLATED)
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asEvent()

    init {
        viewModelScope.launch {
            billingRepository.currentAccountIdFlow.collect { accountId ->
                if (accountId != null) {
                    // アカウントごとの課金状態を監視
                    billingRepository.getPurchaseState(accountId).collect { isPurchased ->
                        _isPremiumPurchased.value = isPurchased
                    }
                } else {
                    _isPremiumPurchased.value = false
                }
            }
        }

        viewModelScope.launch {
            billingRepository.purchaseResult.collect { result ->
                when (result) {
                    is BillingRepository.PurchaseResult.Success -> {
                        _uiEvent.send(UiEvent.PurchaseSuccess)
                    }
                    is BillingRepository.PurchaseResult.Canceled -> {}
                    is BillingRepository.PurchaseResult.Error -> {
                        _uiEvent.send(UiEvent.PurchaseError(result.message))
                    }
                }
            }
        }
    }

    // ログイン状態の確認
    fun isLoggedIn(): Boolean = billingRepository.isLoggedIn()

    // 現在のアカウントIDを設定
    fun setCurrentAccountId(accountId: String?) {
        billingRepository.setCurrentAccountId(accountId)
    }

    // 課金フローの開始
    fun startPremiumPurchase(activity: Activity) {
        billingRepository.launchBillingFlow(activity)
    }

    fun setScrollToLevelIndex(index: Int) {
        savedStateHandle["scroll_index"] = index
    }

    fun clearScrollToLevelIndex() {
        savedStateHandle["scroll_index"] = null
    }

    sealed class UiEvent {
        data object PurchaseSuccess : UiEvent()
        data class PurchaseError(val message: String?) : UiEvent()
    }
}
