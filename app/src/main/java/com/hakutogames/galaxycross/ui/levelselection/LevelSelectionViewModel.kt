package com.hakutogames.galaxycross.ui.levelselection

import android.app.Activity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hakutogames.galaxycross.repository.BillingRepository
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
    private val billingRepository: BillingRepository,
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

    fun setScrollToLevelIndex(index: Int) {
        savedStateHandle["scroll_index"] = index
    }

    fun clearScrollToLevelIndex() {
        savedStateHandle["scroll_index"] = null
    }

    /**
     * プレミアム購入フローを開始する
     * すでに購入済みの場合はイベント通知などで適宜対応
     */
    fun startPremiumPurchase(activity: Activity) {
        viewModelScope.launch {
            // 「すでに購入済み」を同期的にチェックし、購入フローを起動
            kotlin.runCatching {
                if (billingRepository.isPremiumPurchased()) {
                    throw Exception("Premium is already purchased.")
                }
                // 購入フロー開始: ここではまだ「成功」かどうかは不明
                billingRepository.launchBillingFlow(activity)
            }
                .onFailure { throwable ->
                    // BillingClient未初期化やその他同期的エラーがあればここに来る
                    _uiEvent.send(UiEvent.PurchaseError(throwable.message))
                }
        }
    }

    sealed class UiEvent {
        data object PurchaseSuccess : UiEvent()
        data class PurchaseError(val message: String?) : UiEvent()
    }
}
