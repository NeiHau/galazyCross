package com.example.puzzlegame.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.puzzlegame.repository.BillingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LevelSelectionViewModel @Inject constructor(
    private val billingRepository: BillingRepository,
) : ViewModel() {
    // スクロール状態を保持するための状態
    private val _scrollState = MutableStateFlow(ScrollState())
    val scrollState = _scrollState.asStateFlow()

    // 課金状態の管理を追加
    private val _isPremiumPurchased = MutableStateFlow(false)
    val isPremiumPurchased = _isPremiumPurchased.asStateFlow()

    // 課金処理の結果を公開
    val purchaseResult = billingRepository.purchaseResult

    init {
        viewModelScope.launch {
            checkPremiumStatus()
        }
    }

    // プレミアムコンテンツの購入を開始
    fun startPremiumPurchase() {
        viewModelScope.launch {
            billingRepository.startPurchase(PREMIUM_PRODUCT_ID)
        }
    }

    private suspend fun checkPremiumStatus() {
        try {
            // Firestoreから課金状態を取得する処理
            // この実装は環境に応じて適切に実装する必要があります

            // 例: Firestoreのユーザードキュメントから課金状態を確認
            // val userDoc = firestore.collection("users").document(userId).get().await()
            // _isPremiumPurchased.value = userDoc.getBoolean("isPremium") ?: false
        } catch (e: Exception) {
            // エラー処理
        }
    }

    override fun onCleared() {
        super.onCleared()
        billingRepository.cleanup()
    }

    // スクロール状態を更新する関数
    fun updateScrollState(index: Int, offset: Int) {
        _scrollState.value = ScrollState(index, offset)
    }

    // スクロール状態を包括的に管理するデータクラス
    data class ScrollState(
        val index: Int = 0,
        val offset: Int = 0
    )

    companion object {
        private const val PREMIUM_PRODUCT_ID = "premium_levels"
    }
}