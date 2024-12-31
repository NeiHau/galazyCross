package com.example.puzzlegame.ui.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class LevelSelectionViewModel @Inject constructor() : ViewModel() {
    // スクロール状態を包括的に管理するデータクラス
    data class ScrollState(
        val index: Int = 0,
        val offset: Int = 0
    )

    // スクロール状態を保持するための状態
    private val _scrollState = MutableStateFlow(ScrollState())
    val scrollState = _scrollState.asStateFlow()

    // スクロール状態を更新する関数
    fun updateScrollState(index: Int, offset: Int) {
        _scrollState.value = ScrollState(index, offset)
    }
}