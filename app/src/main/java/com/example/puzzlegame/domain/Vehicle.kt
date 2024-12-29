package com.example.puzzlegame.domain

import androidx.compose.ui.geometry.Offset

// 車両を表すデータクラス - イミュータブルなデータモデル
data class Vehicle(
    val id: String,
    val position: Offset,
    val length: Int,
    val isHorizontal: Boolean,
    val isTarget: Boolean = false,
    val isStone: Boolean = false,
    val isTrash: Boolean = false,
    val isTire: Boolean = false,
    val isTree: Boolean = false,
)

fun assignStoneVehicles(vehicles: List<Vehicle>, numberOfStones: Int = 3): List<Vehicle> {
    // Vehicle のインデックスをシャッフルして先頭 numberOfStones 個を取得
    val randomIndices = vehicles.indices.shuffled().take(numberOfStones)

    // 該当する Vehicle にだけフラグを付け替える (コピーを返す)
    return vehicles.mapIndexed { index, vehicle ->
        if (index in randomIndices) {
            vehicle.copy(isStone = true)
        } else {
            vehicle
        }
    }
}
