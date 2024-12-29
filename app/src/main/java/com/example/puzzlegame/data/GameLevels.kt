package com.example.puzzlegame.data

import androidx.compose.ui.geometry.Offset
import com.example.puzzlegame.domain.Vehicle
import com.example.puzzlegame.domain.assignStoneVehicles

object GameLevels {
    val LEVELS = listOf(
        // レベル1
        listOf(
            Vehicle(
                id = "0",
                position = Offset(2f, 2f),
                length = 2,
                isHorizontal = true,
                isTarget = true
            ),
            Vehicle(
                id = "1",
                position = Offset(0f, 0f),
                length = 3,
                isHorizontal = true,
            ),
            Vehicle(
                id = "2",
                position = Offset(3f, 0f),
                length = 2,
                isHorizontal = false,
            ),
            Vehicle(
                id = "3",
                position = Offset(0f, 1f),
                length = 3,
                isHorizontal = false,
            ),
            Vehicle(
                id = "4",
                position = Offset(1f, 1f),
                length = 2,
                isHorizontal = false,
            ),
            Vehicle(
                id = "5",
                position = Offset(4f, 1f),
                length = 2,
                isHorizontal = true,
            ),
            Vehicle(
                id = "6",
                position = Offset(0f, 4f),
                length = 2,
                isHorizontal = false,
            ),
            Vehicle(
                id = "7",
                position = Offset(1f, 3f),
                length = 2,
                isHorizontal = true,
            ),
            Vehicle(
                id = "8",
                position = Offset(1f, 4f),
                length = 2,
                isHorizontal = true,
            ),
            Vehicle(
                id = "9",
                position = Offset(1f, 5f),
                length = 2,
                isHorizontal = true,
            ),
            Vehicle(
                id = "10",
                position = Offset(4f, 2f),
                length = 3,
                isHorizontal = false,
            ),
            Vehicle(
                id = "11",
                position = Offset(3f, 5f),
                length = 2,
                isHorizontal = true,
            ),
        ),

        // レベル2
        listOf(
            Vehicle(
                id = "target",
                position = Offset(0f, 2f),
                length = 2,
                isHorizontal = true,
                isTarget = true,
            ),
            Vehicle(
                id = "v1",
                position = Offset(0f, 0f),
                length = 2,
                isHorizontal = false
            ),
            Vehicle(
                id = "v2",
                position = Offset(1f, 1f),
                length = 3,
                isHorizontal = true,
            ),
            Vehicle(
                id = "v3",
                position = Offset(2f, 0f),
                length = 2,
                isHorizontal = true,
            ),
            Vehicle(
                id = "v4",
                position = Offset(4f, 0f),
                length = 2,
                isHorizontal = true,
            ),
            Vehicle(
                id = "v5",
                position = Offset(5f, 1f),
                length = 2,
                isHorizontal = false,
            ),
            Vehicle(
                id = "v6",
                position = Offset(2f, 2f),
                length = 2,
                isHorizontal = false,
            ),
            Vehicle(
                id = "v7",
                position = Offset(3f, 2f),
                length = 2,
                isHorizontal = false,
            ),
            Vehicle(
                id = "v8",
                position = Offset(4f, 3f),
                length = 2,
                isHorizontal = true,
            ),
            Vehicle(
                id = "v9",
                position = Offset(1f, 4f),
                length = 3,
                isHorizontal = true,
            ),
            Vehicle(
                id = "v10",
                position = Offset(4f, 4f),
                length = 2,
                isHorizontal = false,
            ),
            Vehicle(
                id = "v11",
                position = Offset(5f, 4f),
                length = 2,
                isHorizontal = false,
            ),
            Vehicle(
                id = "v12",
                position = Offset(2f, 5f),
                length = 2,
                isHorizontal = true,
            ),
        ),

        // レベル3
        listOf(
            Vehicle(
                id = "target",
                position = Offset(2f, 2f),
                length = 2,
                isHorizontal = true,
                isTarget = true
            ),
            Vehicle(
                id = "v1",
                position = Offset(0f, 0f),
                length = 2,
                isHorizontal = true,
            ),
            Vehicle(
                id = "v2",
                position = Offset(0f, 1f),
                length = 2,
                isHorizontal = true,
            ),
            Vehicle(
                id = "v3",
                position = Offset(0f, 2f),
                length = 3,
                isHorizontal = false,
            ),
            Vehicle(
                id = "v4",
                position = Offset(0f, 5f),
                length = 2,
                isHorizontal = true,
            ),
            Vehicle(
                id = "v5",
                position = Offset(2f, 0f),
                length = 2,
                isHorizontal = false,
            ),
            Vehicle(
                id = "v6",
                position = Offset(4f, 0f),
                length = 2,
                isHorizontal = true,
            ),
            Vehicle(
                id = "v7",
                position = Offset(4f, 2f),
                length = 3,
                isHorizontal = false,
            ),
            Vehicle(
                id = "v8",
                position = Offset(1f, 3f),
                length = 3,
                isHorizontal = true,
            ),
            Vehicle(
                id = "v9",
                position = Offset(3f, 4f),
                length = 2,
                isHorizontal = false,
            ),
            Vehicle(
                id = "v10",
                position = Offset(4f, 5f),
                length = 2,
                isHorizontal = true,
            ),
            Vehicle(
                id = "v11",
                position = Offset(5f, 3f),
                length = 2,
                isHorizontal = false,
            ),
        ),

        // レベル4
        listOf(
            Vehicle(
                id = "target",
                position = Offset(0f, 2f),
                length = 2,
                isHorizontal = true,
                isTarget = true
            ),
            Vehicle(
                id = "v1",
                position = Offset(1f, 0f),
                length = 2,
                isHorizontal = true ,
            ),
            Vehicle(
                id = "v2",
                position = Offset(0f, 1f),
                length = 3,
                isHorizontal = true,
            ),
            Vehicle(
                id = "v3",
                position = Offset(0f, 3f),
                length = 2,
                isHorizontal = false,
            ),
            Vehicle(
                id = "v4",
                position = Offset(0f, 5f),
                length = 2,
                isHorizontal = true,
            ),
            Vehicle(
                id = "v5",
                position = Offset(2f, 3f),
                length = 2,
                isHorizontal = true,
            ),
            Vehicle(
                id = "v6",
                position = Offset(2f, 4f),
                length = 2,
                isHorizontal = false,
            ),
            Vehicle(
                id = "v7",
                position = Offset(3f, 0f),
                length = 3,
                isHorizontal = false,
            ),
            Vehicle(
                id = "v8",
                position = Offset(4f, 0f),
                length = 2,
                isHorizontal = false,
            ),
            Vehicle(
                id = "v9",
                position = Offset(4f, 2f),
                length = 2,
                isHorizontal = false,
            ),
            Vehicle(
                id = "v10",
                position = Offset(3f, 4f),
                length = 2,
                isHorizontal = true,
            ),
            Vehicle(
                id = "v11",
                position = Offset(5f, 3f),
                length = 3,
                isHorizontal = false,
            ),
        ),

        // レベル5
        listOf(
            Vehicle(
                id = "target",
                position = Offset(1f, 2f),
                length = 2,
                isHorizontal = true,
                isTarget = true
            ),
            Vehicle(
                id = "v1",
                position = Offset(2f, 0f),
                length = 2,
                isHorizontal = false,
            ),
            Vehicle(
                id = "v2",
                position = Offset(3f, 0f),
                length = 3,
                isHorizontal = true,
            ),
            Vehicle(
                id = "v3",
                position = Offset(3f, 1f),
                length = 2,
                isHorizontal = false,
            ),
            Vehicle(
                id = "v4",
                position = Offset(4f, 1f),
                length = 2,
                isHorizontal = true,
            ),
            Vehicle(
                id = "v5",
                position = Offset(4f, 2f),
                length = 3,
                isHorizontal = false,
            ),
            Vehicle(
                id = "v6",
                position = Offset(5f, 2f),
                length = 2,
                isHorizontal = false,
            ),
            Vehicle(
                id = "v7",
                position = Offset(1f, 3f),
                length = 2,
                isHorizontal = false,
            ),
            Vehicle(
                id = "v8",
                position = Offset(0f, 2f),
                length = 2,
                isHorizontal = false,
            ),
            Vehicle(
                id = "v9",
                position = Offset(0f, 4f),
                length = 2,
                isHorizontal = false,
            ),
            Vehicle(
                id = "v10",
                position = Offset(2f, 4f),
                length = 2,
                isHorizontal = true,
            ),
            Vehicle(
                id = "v11",
                position = Offset(1f, 5f),
                length = 2,
                isHorizontal = true,
            ),
            Vehicle(
                id = "v12",
                position = Offset(3f, 5f),
                length = 2,
                isHorizontal = true,
            ),
            Vehicle(
                id = "v13",
                position = Offset(5f, 4f),
                length = 2,
                isHorizontal = false,
            ),
        )
    )

    // 追加: レベルごとに「ランダムで石化した結果」をキャッシュしておくための Map
    private val cachedRandomizedLevels = mutableMapOf<Int, List<Vehicle>>()

    fun getRandomizedLevel(levelIndex: Int = 0): List<Vehicle> {
        if (cachedRandomizedLevels.containsKey(levelIndex)) {
            return cachedRandomizedLevels[levelIndex]!!
        }

        // レベルの元データ
        val originalVehicles = LEVELS[levelIndex]

        // -----------------------------
        // (1) 「長さ2 & 水平方向」
        //     → Stone / Trash に半分ずつ振り分け
        // -----------------------------
        val vehiclesHorizontal2 = originalVehicles.filter {
            it.length == 2 && it.isHorizontal
        }
        val shuffledHorizontal2 = vehiclesHorizontal2.shuffled()
        val halfCountHorizontal = shuffledHorizontal2.size / 2
        val stoneIds = shuffledHorizontal2
            .take(halfCountHorizontal)
            .map { it.id }
        val trashIds = shuffledHorizontal2
            .drop(halfCountHorizontal)
            .map { it.id }

        // -----------------------------
        // (2) 「長さ2 & 垂直方向」
        //     → Tire / Tree に半分ずつ振り分け
        // -----------------------------
        val vehiclesVertical2 = originalVehicles.filter {
            it.length == 2 && !it.isHorizontal
        }
        val shuffledVertical2 = vehiclesVertical2.shuffled()
        val halfCountVertical = shuffledVertical2.size / 2
        val tireIds = shuffledVertical2
            .take(halfCountVertical)
            .map { it.id }
        val treeIds = shuffledVertical2
            .drop(halfCountVertical)
            .map { it.id }

        // -----------------------------
        // (3) 各車両に対してフラグを上書き
        // -----------------------------
        val randomizedResult = originalVehicles.map { vehicle ->
            when (vehicle.id) {
                in stoneIds -> {
                    vehicle.copy(
                        isStone = true,
                        isTrash = false,
                        isTire = false,
                        isTree = false
                    )
                }
                in trashIds -> {
                    vehicle.copy(
                        isStone = false,
                        isTrash = true,
                        isTire = false,
                        isTree = false
                    )
                }
                in tireIds -> {
                    vehicle.copy(
                        isStone = false,
                        isTrash = false,
                        isTire = true,
                        isTree = false
                    )
                }
                in treeIds -> {
                    vehicle.copy(
                        isStone = false,
                        isTrash = false,
                        isTire = false,
                        isTree = true
                    )
                }
                else -> {
                    // いずれのグループにも該当しない車両
                    vehicle
                }
            }
        }

        cachedRandomizedLevels[levelIndex] = randomizedResult
        return randomizedResult
    }
}