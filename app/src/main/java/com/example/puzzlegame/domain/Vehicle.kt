package com.example.puzzlegame.domain

import androidx.compose.ui.geometry.Offset

// 車両を表すデータクラス - イミュータブルなデータモデル
data class Vehicle(
    val id: String,
    val position: Offset,
    val length: Int,
    val isHorizontal: Boolean,
    val isTarget: Boolean = false,
    val imageIndex: Int = 0
)
