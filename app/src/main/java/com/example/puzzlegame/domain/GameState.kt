package com.example.puzzlegame.domain

// ゲーム状態を表すデータクラス - 状態管理を整理
data class GameState(
    val vehicles: List<Vehicle> = emptyList(),
    val selectedVehicleId: String? = null,
    val isGameComplete: Boolean = false
)