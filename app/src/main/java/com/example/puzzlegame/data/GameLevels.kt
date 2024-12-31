package com.example.puzzlegame.data

import androidx.compose.ui.geometry.Offset
import com.example.puzzlegame.domain.Vehicle

object GameLevels {
    // 車両リストにプラネットアイコンを順番に割り当てる関数
    private fun assignPlanetIcons(vehicles: List<Vehicle>): List<Vehicle> {
        var currentPlanetIndex = 1  // ic_planet_1 から開始

        return vehicles.map { vehicle ->
            if (vehicle.isTarget) {
                // ターゲット車両はそのまま（アイコンは既に設定済み）
                vehicle
            } else {
                // 非ターゲット車両には順番にプラネットアイコンを割り当て
                vehicle.copy(
                    imageIndex = currentPlanetIndex++
                ).also {
                    // インデックスが14を超えた場合は1に戻す
                    if (currentPlanetIndex > 14) {
                        currentPlanetIndex = 1
                    }
                }
            }
        }
    }

    // 各レベルのデータを修正した完全な実装
    val LEVELS = listOf(
        // レベル1
        assignPlanetIcons(listOf(
            Vehicle(id = "0", position = Offset(2f, 2f), length = 2, isHorizontal = false,
                isTarget = true),
            Vehicle(id = "1", position = Offset(0f, 3f), length = 3, isHorizontal = false),
            Vehicle(id = "2", position = Offset(0f, 2f), length = 2, isHorizontal = true),
            Vehicle(id = "3", position = Offset(1f, 5f), length = 3, isHorizontal = true),
            Vehicle(id = "4", position = Offset(1f, 4f), length = 2, isHorizontal = true),
            Vehicle(id = "5", position = Offset(1f, 0f), length = 2, isHorizontal = false),
            Vehicle(id = "6", position = Offset(4f, 5f), length = 2, isHorizontal = true),
            Vehicle(id = "7", position = Offset(3f, 3f), length = 2, isHorizontal = false),
            Vehicle(id = "8", position = Offset(4f, 3f), length = 2, isHorizontal = false),
            Vehicle(id = "9", position = Offset(5f, 3f), length = 2, isHorizontal = false),
            Vehicle(id = "10", position = Offset(2f, 1f), length = 3, isHorizontal = true),
            Vehicle(id = "11", position = Offset(5f, 1f), length = 2, isHorizontal = false)
        )),

        // レベル2
        assignPlanetIcons(listOf(
            Vehicle(id = "target", position = Offset(0f, 2f), length = 2, isHorizontal = true,
                isTarget = true),
            Vehicle(id = "v1", position = Offset(0f, 0f), length = 2, isHorizontal = false),
            Vehicle(id = "v2", position = Offset(1f, 1f), length = 3, isHorizontal = true),
            Vehicle(id = "v3", position = Offset(2f, 0f), length = 2, isHorizontal = true),
            Vehicle(id = "v4", position = Offset(4f, 0f), length = 2, isHorizontal = true),
            Vehicle(id = "v5", position = Offset(5f, 1f), length = 2, isHorizontal = false),
            Vehicle(id = "v6", position = Offset(2f, 2f), length = 2, isHorizontal = false),
            Vehicle(id = "v7", position = Offset(3f, 2f), length = 2, isHorizontal = false),
            Vehicle(id = "v8", position = Offset(4f, 3f), length = 2, isHorizontal = true),
            Vehicle(id = "v9", position = Offset(1f, 4f), length = 3, isHorizontal = true),
            Vehicle(id = "v10", position = Offset(4f, 4f), length = 2, isHorizontal = false),
            Vehicle(id = "v11", position = Offset(5f, 4f), length = 2, isHorizontal = false),
            Vehicle(id = "v12", position = Offset(2f, 5f), length = 2, isHorizontal = true)
        )),

        // レベル3
        assignPlanetIcons(listOf(
            Vehicle(id = "target", position = Offset(2f, 2f), length = 2, isHorizontal = true,
                isTarget = true),
            Vehicle(id = "v1", position = Offset(0f, 0f), length = 2, isHorizontal = true),
            Vehicle(id = "v2", position = Offset(0f, 1f), length = 2, isHorizontal = true),
            Vehicle(id = "v3", position = Offset(0f, 2f), length = 3, isHorizontal = false),
            Vehicle(id = "v4", position = Offset(0f, 5f), length = 2, isHorizontal = true),
            Vehicle(id = "v5", position = Offset(2f, 0f), length = 2, isHorizontal = false),
            Vehicle(id = "v6", position = Offset(4f, 0f), length = 2, isHorizontal = true),
            Vehicle(id = "v7", position = Offset(4f, 2f), length = 3, isHorizontal = false),
            Vehicle(id = "v8", position = Offset(1f, 3f), length = 3, isHorizontal = true),
            Vehicle(id = "v9", position = Offset(3f, 4f), length = 2, isHorizontal = false),
            Vehicle(id = "v10", position = Offset(4f, 5f), length = 2, isHorizontal = true),
            Vehicle(id = "v11", position = Offset(5f, 3f), length = 2, isHorizontal = false)
        )),

        // レベル4
        assignPlanetIcons(listOf(
            Vehicle(id = "target", position = Offset(0f, 2f), length = 2, isHorizontal = true,
                isTarget = true),
            Vehicle(id = "v1", position = Offset(1f, 0f), length = 2, isHorizontal = true),
            Vehicle(id = "v2", position = Offset(0f, 1f), length = 3, isHorizontal = true),
            Vehicle(id = "v3", position = Offset(0f, 3f), length = 2, isHorizontal = false),
            Vehicle(id = "v4", position = Offset(0f, 5f), length = 2, isHorizontal = true),
            Vehicle(id = "v5", position = Offset(2f, 3f), length = 2, isHorizontal = true),
            Vehicle(id = "v6", position = Offset(2f, 4f), length = 2, isHorizontal = false),
            Vehicle(id = "v7", position = Offset(3f, 0f), length = 3, isHorizontal = false),
            Vehicle(id = "v8", position = Offset(4f, 0f), length = 2, isHorizontal = false),
            Vehicle(id = "v9", position = Offset(4f, 2f), length = 2, isHorizontal = false),
            Vehicle(id = "v10", position = Offset(3f, 4f), length = 2, isHorizontal = true),
            Vehicle(id = "v11", position = Offset(5f, 3f), length = 3, isHorizontal = false)
        )),

        // レベル5
        assignPlanetIcons(listOf(
            Vehicle(id = "target", position = Offset(1f, 2f), length = 2, isHorizontal = true,
                isTarget = true),
            Vehicle(id = "v1", position = Offset(2f, 0f), length = 2, isHorizontal = false),
            Vehicle(id = "v2", position = Offset(3f, 0f), length = 3, isHorizontal = true),
            Vehicle(id = "v3", position = Offset(3f, 1f), length = 2, isHorizontal = false),
            Vehicle(id = "v4", position = Offset(4f, 1f), length = 2, isHorizontal = true),
            Vehicle(id = "v5", position = Offset(4f, 2f), length = 3, isHorizontal = false),
            Vehicle(id = "v6", position = Offset(5f, 2f), length = 2, isHorizontal = false),
            Vehicle(id = "v7", position = Offset(1f, 3f), length = 2, isHorizontal = false),
            Vehicle(id = "v8", position = Offset(0f, 2f), length = 2, isHorizontal = false),
            Vehicle(id = "v9", position = Offset(0f, 4f), length = 2, isHorizontal = false),
            Vehicle(id = "v10", position = Offset(2f, 4f), length = 2, isHorizontal = true),
            Vehicle(id = "v11", position = Offset(1f, 5f), length = 2, isHorizontal = true),
            Vehicle(id = "v12", position = Offset(3f, 5f), length = 2, isHorizontal = true),
            Vehicle(id = "v13", position = Offset(5f, 4f), length = 2, isHorizontal = false)
        ))
    )

    /**
     * 指定されたレベルの車両リストを取得します。
     * このメソッドは、ViewModelから呼び出されることを想定しています。
     *
     * @param levelIndex 取得したいレベルのインデックス（0から開始）
     * @return 指定されたレベルの車両リスト
     * @throws IndexOutOfBoundsException レベルインデックスが範囲外の場合
     */
    fun getRandomizedLevel(levelIndex: Int): List<Vehicle> {
        // レベルの範囲チェック
        require(levelIndex in LEVELS.indices) {
            "Invalid level index: $levelIndex. Available levels: 0..${LEVELS.size - 1}"
        }

        // LEVELSは既にassignPlanetIconsが適用された状態で保存されているため、
        // そのまま対応するレベルを返すだけで良い
        return LEVELS[levelIndex]
    }
}