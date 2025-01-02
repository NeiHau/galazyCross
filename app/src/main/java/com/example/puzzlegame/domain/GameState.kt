package com.example.puzzlegame.domain

data class GameState(
    val gridItems: List<GridItem> = emptyList(),
    val selectedVehicleId: String? = null,
    val isGameComplete: Boolean = false
)