package com.example.puzzlegame.data

import androidx.compose.ui.geometry.Offset
import com.example.puzzlegame.domain.GridItem

object GameLevels {
    private fun assignPlanetIcons(gridItems: List<GridItem>): List<GridItem> {
        var currentPlanetIndex = 1

        return gridItems.map { vehicle ->
            if (vehicle.isTarget) {
                vehicle
            } else {
                vehicle.copy(
                    imageIndex = currentPlanetIndex++
                ).also {
                    if (currentPlanetIndex > 14) {
                        currentPlanetIndex = 1
                    }
                }
            }
        }
    }

    // チュートリアル用の定数
    const val TUTORIAL_LEVEL_INDEX = -1 // 通常のレベルとは異なるインデックスを使用

    // チュートリアルレベルの定義
    private val TUTORIAL_LEVEL = assignPlanetIcons(listOf(
        // ターゲットとなる宇宙船（上下移動のみ必要な簡単な配置）
        GridItem(
            id = "0",
            position = Offset(2f, 4f),
            length = 2,
            isHorizontal = false,
            isTarget = true
        ),
        // 邪魔になる惑星は最小限に
        GridItem(
            id = "1",
            position = Offset(2f, 2f),
            length = 2,
            isHorizontal = true
        ),
        GridItem(
            id = "2",
            position = Offset(2f, 3f),
            length = 3,
            isHorizontal = true
        ),
        GridItem(
            id = "3",
            position = Offset(1f, 0f),
            length = 3,
            isHorizontal = false,
        ),
    ))

    val LEVELS = listOf(
        // レベル1(3)
        assignPlanetIcons(listOf(
            GridItem(id = "0", position = Offset(2f, 4f), length = 2, isHorizontal = false,
                isTarget = true),
            GridItem(id = "1", position = Offset(0f, 1f), length = 2, isHorizontal = true),
            GridItem(id = "2", position = Offset(3f, 1f), length = 2, isHorizontal = true),
            GridItem(id = "3", position = Offset(0f, 2f), length = 2, isHorizontal = false),
            GridItem(id = "4", position = Offset(1f, 2f), length = 2, isHorizontal = false),
            GridItem(id = "5", position = Offset(2f, 2f), length = 3, isHorizontal = true),
            GridItem(id = "6", position = Offset(2f, 3f), length = 2, isHorizontal = true),
            GridItem(id = "7", position = Offset(4f, 3f), length = 2, isHorizontal = true),
            GridItem(id = "8", position = Offset(0f, 4f), length = 2, isHorizontal = true),
            GridItem(id = "9", position = Offset(0f, 5f), length = 2, isHorizontal = true),
        )),

        // レベル2(5)
        assignPlanetIcons(listOf(
            GridItem(id = "0", position = Offset(2f, 4f), length = 2, isHorizontal = false,
                isTarget = true),
            GridItem(id = "1", position = Offset(0f, 1f), length = 3, isHorizontal = false),
            GridItem(id = "2", position = Offset(1f, 1f), length = 3, isHorizontal = true),
            GridItem(id = "3", position = Offset(2f, 0f), length = 2, isHorizontal = true),
            GridItem(id = "4", position = Offset(4f, 0f), length = 2, isHorizontal = false),
            GridItem(id = "5", position = Offset(5f, 0f), length = 2, isHorizontal = false),
            GridItem(id = "6", position = Offset(1f, 3f), length = 3, isHorizontal = false),
            GridItem(id = "7", position = Offset(3f, 3f), length = 3, isHorizontal = false),
            GridItem(id = "8", position = Offset(4f, 3f), length = 2, isHorizontal = true),
            GridItem(id = "9", position = Offset(4f, 5f), length = 2, isHorizontal = true),
        )),

        // レベル3(6)
        assignPlanetIcons(listOf(
            GridItem(id = "0", position = Offset(2f, 4f), length = 2, isHorizontal = false,
                isTarget = true),
            GridItem(id = "1", position = Offset(1f, 0f), length = 2, isHorizontal = true),
            GridItem(id = "2", position = Offset(3f, 0f), length = 3, isHorizontal = true),
            GridItem(id = "3", position = Offset(3f, 1f), length = 2, isHorizontal = false),
            GridItem(id = "4", position = Offset(4f, 1f), length = 2, isHorizontal = true),
            GridItem(id = "5", position = Offset(4f, 2f), length = 2, isHorizontal = true),
            GridItem(id = "6", position = Offset(1f, 3f), length = 3, isHorizontal = true),
            GridItem(id = "7", position = Offset(4f, 3f), length = 2, isHorizontal = false),
            GridItem(id = "8", position = Offset(5f, 3f), length = 3, isHorizontal = false),
            GridItem(id = "9", position = Offset(3f, 5f), length = 2, isHorizontal = true),
        )),

        // レベル4(8)
        assignPlanetIcons(listOf(
            GridItem(id = "0", position = Offset(2f, 2f), length = 2, isHorizontal = false,
                isTarget = true),
            GridItem(id = "1", position = Offset(0f, 0f), length = 2, isHorizontal = false),
            GridItem(id = "2", position = Offset(1f, 0f), length = 2, isHorizontal = true),
            GridItem(id = "3", position = Offset(1f, 1f), length = 2, isHorizontal = true),
            GridItem(id = "4", position = Offset(4f, 0f), length = 3, isHorizontal = false),
            GridItem(id = "5", position = Offset(0f, 2f), length = 2, isHorizontal = true),
            GridItem(id = "6", position = Offset(0f, 3f), length = 2, isHorizontal = true),
            GridItem(id = "7", position = Offset(0f, 4f), length = 2, isHorizontal = false),
            GridItem(id = "8", position = Offset(1f, 4f), length = 2, isHorizontal = false),
            GridItem(id = "9", position = Offset(2f, 4f), length = 3, isHorizontal = true),
        )),

        // レベル5(10)
        assignPlanetIcons(listOf(
            GridItem(id = "0", position = Offset(2f, 1f), length = 2, isHorizontal = false,
                isTarget = true),
            GridItem(id = "1", position = Offset(0f, 0f), length = 2, isHorizontal = false),
            GridItem(id = "2", position = Offset(1f, 0f), length = 2, isHorizontal = true),
            GridItem(id = "3", position = Offset(3f, 0f), length = 3, isHorizontal = false),
            GridItem(id = "4", position = Offset(4f, 0f), length = 2, isHorizontal = true),
            GridItem(id = "5", position = Offset(4f, 1f), length = 2, isHorizontal = true),
            GridItem(id = "6", position = Offset(3f, 2f), length = 2, isHorizontal = true),
            GridItem(id = "7", position = Offset(5f, 2f), length = 3, isHorizontal = false),
            GridItem(id = "8", position = Offset(0f, 4f), length = 3, isHorizontal = true),
            GridItem(id = "9", position = Offset(3f, 4f), length = 2, isHorizontal = true),
            GridItem(id = "10", position = Offset(0f, 2f), length = 2, isHorizontal = false),
        )),

        // レベル6(11)
        assignPlanetIcons(listOf(
            GridItem(id = "0", position = Offset(2f, 4f), length = 2, isHorizontal = false,
                isTarget = true),
            GridItem(id = "1", position = Offset(0f, 0f), length = 2, isHorizontal = true),
            GridItem(id = "2", position = Offset(2f, 0f), length = 2, isHorizontal = true),
            GridItem(id = "3", position = Offset(3f, 1f), length = 2, isHorizontal = false),
            GridItem(id = "4", position = Offset(4f, 1f), length = 2, isHorizontal = true),
            GridItem(id = "5", position = Offset(0f, 3f), length = 3, isHorizontal = false),
            GridItem(id = "6", position = Offset(1f, 3f), length = 3, isHorizontal = true),
            GridItem(id = "7", position = Offset(4f, 3f), length = 3, isHorizontal = false),
            GridItem(id = "8", position = Offset(5f, 3f), length = 2, isHorizontal = false),
            GridItem(id = "9", position = Offset(0f, 2f), length = 2, isHorizontal = true),
        )),

        // レベル7(13)
        assignPlanetIcons(listOf(
            GridItem(id = "0", position = Offset(2f, 4f), length = 2, isHorizontal = false,
                isTarget = true),
            GridItem(id = "1", position = Offset(0f, 0f), length = 3, isHorizontal = false),
            GridItem(id = "2", position = Offset(1f, 1f), length = 2, isHorizontal = true),
            GridItem(id = "2", position = Offset(1f, 0f), length = 2, isHorizontal = true),
            GridItem(id = "3", position = Offset(1f, 2f), length = 2, isHorizontal = true),
            GridItem(id = "4", position = Offset(1f, 3f), length = 2, isHorizontal = true),
            GridItem(id = "5", position = Offset(3f, 1f), length = 2, isHorizontal = false),
            GridItem(id = "6", position = Offset(5f, 1f), length = 3, isHorizontal = false),
            GridItem(id = "7", position = Offset(0f, 3f), length = 2, isHorizontal = false),
            GridItem(id = "8", position = Offset(1f, 3f), length = 3, isHorizontal = false),
            GridItem(id = "9", position = Offset(2f, 3f), length = 2, isHorizontal = true),
            GridItem(id = "10", position = Offset(4f, 3f), length = 2, isHorizontal = false),
            GridItem(id = "11", position = Offset(5f, 4f), length = 2, isHorizontal = false),
            GridItem(id = "12", position = Offset(3f, 5f), length = 2, isHorizontal = true),
            GridItem(id = "13", position = Offset(3f, 0f), length = 3, isHorizontal = true),
        )),

        // レベル8(14)
        assignPlanetIcons(listOf(
            GridItem(id = "0", position = Offset(2f, 2f), length = 2, isHorizontal = false,
                isTarget = true),
            GridItem(id = "1", position = Offset(1f, 0f), length = 3, isHorizontal = false),
            GridItem(id = "2", position = Offset(4f, 0f), length = 2, isHorizontal = true),
            GridItem(id = "3", position = Offset(2f, 1f), length = 2, isHorizontal = true),
            GridItem(id = "4", position = Offset(4f, 1f), length = 3, isHorizontal = false),
            GridItem(id = "5", position = Offset(0f, 3f), length = 2, isHorizontal = true),
            GridItem(id = "6", position = Offset(0f, 4f), length = 2, isHorizontal = false),
            GridItem(id = "7", position = Offset(1f, 5f), length = 2, isHorizontal = true),
            GridItem(id = "8", position = Offset(4f, 4f), length = 2, isHorizontal = false),
        )),

        // レベル9(16)
        assignPlanetIcons(listOf(
            GridItem(id = "0", position = Offset(2f, 4f), length = 2, isHorizontal = false,
                isTarget = true),
            GridItem(id = "1", position = Offset(0f, 0f), length = 2, isHorizontal = false),
            GridItem(id = "2", position = Offset(1f, 0f), length = 2, isHorizontal = false),
            GridItem(id = "3", position = Offset(2f, 0f), length = 2, isHorizontal = true),
            GridItem(id = "4", position = Offset(4f, 0f), length = 2, isHorizontal = true),
            GridItem(id = "5", position = Offset(2f, 1f), length = 2, isHorizontal = true),
            GridItem(id = "6", position = Offset(4f, 1f), length = 2, isHorizontal = true),
            GridItem(id = "7", position = Offset(0f, 2f), length = 2, isHorizontal = true),
            GridItem(id = "8", position = Offset(2f, 2f), length = 2, isHorizontal = true),
            GridItem(id = "9", position = Offset(5f, 2f), length = 3, isHorizontal = false),
            GridItem(id = "10", position = Offset(4f, 2f), length = 3, isHorizontal = false),
            GridItem(id = "11", position = Offset(3f, 4f), length = 2, isHorizontal = false),
            GridItem(id = "12", position = Offset(4f, 5f), length = 2, isHorizontal = true),
            GridItem(id = "13", position = Offset(2f, 3f), length = 2, isHorizontal = true),
        )),

        // レベル10(17)
        assignPlanetIcons(listOf(
            GridItem(id = "0", position = Offset(2f, 4f), length = 2, isHorizontal = false,
                isTarget = true),
            GridItem(id = "1", position = Offset(0f, 0f), length = 2, isHorizontal = true),
            GridItem(id = "2", position = Offset(2f, 0f), length = 2, isHorizontal = true),
            GridItem(id = "3", position = Offset(5f, 0f), length = 2, isHorizontal = false),
            GridItem(id = "4", position = Offset(0f, 1f), length = 2, isHorizontal = false),
            GridItem(id = "5", position = Offset(1f, 1f), length = 2, isHorizontal = false),
            GridItem(id = "6", position = Offset(3f, 1f), length = 2, isHorizontal = true),
            GridItem(id = "7", position = Offset(2f, 2f), length = 3, isHorizontal = true),
            GridItem(id = "8", position = Offset(5f, 2f), length = 3, isHorizontal = false),
            GridItem(id = "9", position = Offset(0f, 3f), length = 3, isHorizontal = false),
            GridItem(id = "10", position = Offset(1f, 3f), length = 2, isHorizontal = true),
            GridItem(id = "11", position = Offset(4f, 3f), length = 2, isHorizontal = false),
            GridItem(id = "12", position = Offset(1f, 4f), length = 2, isHorizontal = false),
            GridItem(id = "13", position = Offset(4f, 5f), length = 2, isHorizontal = true),
        )),

        // レベル11(19)
        assignPlanetIcons(listOf(
            GridItem(id = "0", position = Offset(2f, 4f), length = 2, isHorizontal = false,
                isTarget = true),
            GridItem(id = "1", position = Offset(0f, 0f), length = 3, isHorizontal = false),
            GridItem(id = "2", position = Offset(1f, 0f), length = 2, isHorizontal = true),
            GridItem(id = "3", position = Offset(3f, 0f), length = 3, isHorizontal = true),
            GridItem(id = "4", position = Offset(3f, 1f), length = 2, isHorizontal = true),
            GridItem(id = "5", position = Offset(5f, 1f), length = 3, isHorizontal = false),
            GridItem(id = "6", position = Offset(4f, 2f), length = 2, isHorizontal = false),
            GridItem(id = "7", position = Offset(0f, 3f), length = 2, isHorizontal = true),
            GridItem(id = "8", position = Offset(2f, 3f), length = 2, isHorizontal = true),
            GridItem(id = "9", position = Offset(3f, 4f), length = 2, isHorizontal = false),
            GridItem(id = "10", position = Offset(4f, 4f), length = 2, isHorizontal = true),
        )),

        // レベル12(20)
        assignPlanetIcons(listOf(
            GridItem(id = "0", position = Offset(2f, 4f), length = 2, isHorizontal = false,
                isTarget = true),
            GridItem(id = "1", position = Offset(0f, 0f), length = 2, isHorizontal = false),
            GridItem(id = "2", position = Offset(0f, 2f), length = 2, isHorizontal = true),
            GridItem(id = "3", position = Offset(2f, 0f), length = 2, isHorizontal = true),
            GridItem(id = "4", position = Offset(4f, 0f), length = 2, isHorizontal = true),
            GridItem(id = "5", position = Offset(3f, 1f), length = 2, isHorizontal = true),
            GridItem(id = "6", position = Offset(2f, 2f), length = 2, isHorizontal = true),
            GridItem(id = "7", position = Offset(4f, 2f), length = 3, isHorizontal = false),
            GridItem(id = "8", position = Offset(2f, 3f), length = 2, isHorizontal = true),
            GridItem(id = "9", position = Offset(4f, 5f), length = 2, isHorizontal = true),
        )),

        // レベル13(22)
        assignPlanetIcons(listOf(
            GridItem(id = "0", position = Offset(2f, 4f), length = 2, isHorizontal = false,
                isTarget = true),
            GridItem(id = "1", position = Offset(0f, 0f), length = 2, isHorizontal = true),
            GridItem(id = "2", position = Offset(2f, 0f), length = 3, isHorizontal = true),
            GridItem(id = "3", position = Offset(5f, 0f), length = 2, isHorizontal = false),
            GridItem(id = "4", position = Offset(0f, 1f), length = 2, isHorizontal = true),
            GridItem(id = "5", position = Offset(2f, 1f), length = 2, isHorizontal = true),
            GridItem(id = "6", position = Offset(0f, 2f), length = 2, isHorizontal = false),
            GridItem(id = "7", position = Offset(1f, 2f), length = 2, isHorizontal = true),
            GridItem(id = "8", position = Offset(3f, 2f), length = 2, isHorizontal = true),
            GridItem(id = "9", position = Offset(5f, 2f), length = 3, isHorizontal = false),
            GridItem(id = "10", position = Offset(1f, 3f), length = 2, isHorizontal = true),
            GridItem(id = "11", position = Offset(3f, 3f), length = 2, isHorizontal = true),
            GridItem(id = "12", position = Offset(0f, 4f), length = 2, isHorizontal = true),
            GridItem(id = "13", position = Offset(3f, 5f), length = 3, isHorizontal = true),
        )),

        // レベル14(24)
        assignPlanetIcons(listOf(
            GridItem(id = "0", position = Offset(2f, 3f), length = 2, isHorizontal = false,
                isTarget = true),
            GridItem(id = "1", position = Offset(0f, 0f), length = 3, isHorizontal = false),
            GridItem(id = "2", position = Offset(3f, 0f), length = 3, isHorizontal = true),
            GridItem(id = "3", position = Offset(1f, 2f), length = 2, isHorizontal = true),
            GridItem(id = "4", position = Offset(3f, 2f), length = 2, isHorizontal = false),
            GridItem(id = "5", position = Offset(5f, 2f), length = 2, isHorizontal = false),
            GridItem(id = "6", position = Offset(0f, 3f), length = 2, isHorizontal = true),
            GridItem(id = "7", position = Offset(0f, 5f), length = 3, isHorizontal = true),
            GridItem(id = "8", position = Offset(3f, 4f), length = 2, isHorizontal = false),
            GridItem(id = "9", position = Offset(5f, 4f), length = 2, isHorizontal = false),
        )),

        // レベル15(25)
        assignPlanetIcons(listOf(
            GridItem(id = "0", position = Offset(2f, 3f), length = 2, isHorizontal = false,
                isTarget = true),
            GridItem(id = "1", position = Offset(0f, 0f), length = 2, isHorizontal = true),
            GridItem(id = "2", position = Offset(0f, 1f), length = 2, isHorizontal = true),
            GridItem(id = "3", position = Offset(2f, 0f), length = 3, isHorizontal = true),
            GridItem(id = "4", position = Offset(3f, 1f), length = 2, isHorizontal = false),
            GridItem(id = "5", position = Offset(0f, 2f), length = 3, isHorizontal = true),
            GridItem(id = "6", position = Offset(3f, 3f), length = 3, isHorizontal = true),
            GridItem(id = "7", position = Offset(0f, 4f), length = 2, isHorizontal = false),
            GridItem(id = "8", position = Offset(2f, 5f), length = 2, isHorizontal = true),
            GridItem(id = "9", position = Offset(4f, 4f), length = 2, isHorizontal = false),
            GridItem(id = "10", position = Offset(5f, 4f), length = 2, isHorizontal = false),
        )),

        // ここから先は課金コンテンツにする
        // 難しい
        // レベル16
        assignPlanetIcons(listOf(
            GridItem(id = "0", position = Offset(2f, 2f), length = 2, isHorizontal = false,
                isTarget = true),
            GridItem(id = "1", position = Offset(0f, 3f), length = 3, isHorizontal = false),
            GridItem(id = "2", position = Offset(0f, 2f), length = 2, isHorizontal = true),
            GridItem(id = "3", position = Offset(1f, 5f), length = 3, isHorizontal = true),
            GridItem(id = "4", position = Offset(1f, 4f), length = 2, isHorizontal = true),
            GridItem(id = "5", position = Offset(1f, 0f), length = 2, isHorizontal = false),
            GridItem(id = "6", position = Offset(4f, 5f), length = 2, isHorizontal = true),
            GridItem(id = "7", position = Offset(3f, 3f), length = 2, isHorizontal = false),
            GridItem(id = "8", position = Offset(4f, 3f), length = 2, isHorizontal = false),
            GridItem(id = "9", position = Offset(5f, 3f), length = 2, isHorizontal = false),
            GridItem(id = "10", position = Offset(2f, 1f), length = 3, isHorizontal = true),
            GridItem(id = "11", position = Offset(5f, 1f), length = 2, isHorizontal = false),
            GridItem(id = "12", position = Offset(4f, 0f), length = 2, isHorizontal = true),
        )),

        // レベル17
        assignPlanetIcons(listOf(
            GridItem(id = "target", position = Offset(2f, 4f), length = 2, isHorizontal = false,
                isTarget = true),
            GridItem(id = "v1", position = Offset(0f, 5f), length = 2, isHorizontal = true),
            GridItem(id = "v2", position = Offset(1f, 2f), length = 3, isHorizontal = false),
            GridItem(id = "v3", position = Offset(0f, 2f), length = 2, isHorizontal = false),
            GridItem(id = "v4", position = Offset(0f, 0f), length = 2, isHorizontal = false),
            GridItem(id = "v5", position = Offset(1f, 0f), length = 2, isHorizontal = true),
            GridItem(id = "v6", position = Offset(2f, 3f), length = 2, isHorizontal = true),
            GridItem(id = "v7", position = Offset(2f, 2f), length = 2, isHorizontal = true),
            GridItem(id = "v8", position = Offset(3f, 0f), length = 2, isHorizontal = false),
            GridItem(id = "v9", position = Offset(4f, 2f), length = 3, isHorizontal = false),
            GridItem(id = "v10", position = Offset(4f, 1f), length = 2, isHorizontal = true),
            GridItem(id = "v11", position = Offset(4f, 0f), length = 2, isHorizontal = true),
            GridItem(id = "v12", position = Offset(5f, 2f), length = 2, isHorizontal = false),
        )),

        // レベル18
        assignPlanetIcons(listOf(
            GridItem(id = "target", position = Offset(2f, 2f), length = 2, isHorizontal = false,
                isTarget = true),
            GridItem(id = "v1", position = Offset(0f, 4f), length = 2, isHorizontal = false),
            GridItem(id = "v2", position = Offset(1f, 4f), length = 2, isHorizontal = false),
            GridItem(id = "v3", position = Offset(2f, 5f), length = 3, isHorizontal = true),
            GridItem(id = "v4", position = Offset(5f, 4f), length = 2, isHorizontal = false),
            GridItem(id = "v5", position = Offset(0f, 3f), length = 2, isHorizontal = true),
            GridItem(id = "v6", position = Offset(0f, 0f), length = 2, isHorizontal = false),
            GridItem(id = "v7", position = Offset(2f, 1f), length = 3, isHorizontal = true),
            GridItem(id = "v8", position = Offset(3f, 2f), length = 3, isHorizontal = false),
            GridItem(id = "v9", position = Offset(4f, 2f), length = 2, isHorizontal = true),
            GridItem(id = "v10", position = Offset(5f, 0f), length = 2, isHorizontal = false),
            GridItem(id = "v11", position = Offset(3f, 0f), length = 2, isHorizontal = true),
        )),

        // レベル19
        assignPlanetIcons(listOf(
            GridItem(id = "target", position = Offset(2f, 4f), length = 2, isHorizontal = false,
                isTarget = true),
            GridItem(id = "v1", position = Offset(0f, 3f), length = 2, isHorizontal = false),
            GridItem(id = "v2", position = Offset(1f, 3f), length = 3, isHorizontal = false),
            GridItem(id = "v3", position = Offset(3f, 5f), length = 2, isHorizontal = true),
            GridItem(id = "v4", position = Offset(5f, 4f), length = 2, isHorizontal = false),
            GridItem(id = "v5", position = Offset(3f, 2f), length = 2, isHorizontal = false),
            GridItem(id = "v6", position = Offset(4f, 3f), length = 2, isHorizontal = true),
            GridItem(id = "v7", position = Offset(0f, 2f), length = 3, isHorizontal = true),
            GridItem(id = "v8", position = Offset(0f, 1f), length = 2, isHorizontal = true),
            GridItem(id = "v9", position = Offset(2f, 1f), length = 2, isHorizontal = true),
            GridItem(id = "v10", position = Offset(4f, 1f), length = 2, isHorizontal = false),
            GridItem(id = "v11", position = Offset(3f, 0f), length = 3, isHorizontal = true),
        )),

        // レベル20
        assignPlanetIcons(listOf(
            GridItem(id = "target", position = Offset(2f, 3f), length = 2, isHorizontal = false,
                isTarget = true),
            GridItem(id = "v1", position = Offset(1f, 2f), length = 2, isHorizontal = true),
            GridItem(id = "v2", position = Offset(0f, 0f), length = 3, isHorizontal = false),
            GridItem(id = "v3", position = Offset(0f, 3f), length = 2, isHorizontal = true),
            GridItem(id = "v4", position = Offset(4f, 4f), length = 2, isHorizontal = false),
            GridItem(id = "v5", position = Offset(3f, 4f), length = 2, isHorizontal = true),
            GridItem(id = "v6", position = Offset(4f, 5f), length = 2, isHorizontal = true),
            GridItem(id = "v7", position = Offset(2f, 1f), length = 3, isHorizontal = true),
            GridItem(id = "v8", position = Offset(4f, 0f), length = 2, isHorizontal = true),
            GridItem(id = "v9", position = Offset(2f, 0f), length = 2, isHorizontal = true),
            GridItem(id = "v10", position = Offset(1f, 0f), length = 2, isHorizontal = false),
            GridItem(id = "v11", position = Offset(5f, 1f), length = 2, isHorizontal = false),
            GridItem(id = "v12", position = Offset(4f, 2f), length = 2, isHorizontal = false),
            GridItem(id = "v13", position = Offset(2f, 5f), length = 2, isHorizontal = true),
            GridItem(id = "v13", position = Offset(5f, 3f), length = 2, isHorizontal = false),
        )),

        // レベル21
        assignPlanetIcons(listOf(
            GridItem(id = "target", position = Offset(2f, 1f), length = 2, isHorizontal = false,
                isTarget = true),
            GridItem(id = "v1", position = Offset(0f, 0f), length = 2, isHorizontal = true),
            GridItem(id = "v2", position = Offset(2f, 0f), length = 3, isHorizontal = true),
            GridItem(id = "v3", position = Offset(4f, 0f), length = 2, isHorizontal = false),
            GridItem(id = "v4", position = Offset(0f, 1f), length = 2, isHorizontal = true),
            GridItem(id = "v5", position = Offset(3f, 1f), length = 3, isHorizontal = false),
            GridItem(id = "v6", position = Offset(0f, 2f), length = 2, isHorizontal = false),
            GridItem(id = "v7", position = Offset(4f, 2f), length = 2, isHorizontal = true),
            GridItem(id = "v8", position = Offset(4f, 3f), length = 2, isHorizontal = true),
            GridItem(id = "v9", position = Offset(0f, 4f), length = 2, isHorizontal = false),
            GridItem(id = "v10", position = Offset(1f, 4f), length = 2, isHorizontal = false),
            GridItem(id = "v11", position = Offset(3f, 5f), length = 3, isHorizontal = true),
        )),

        // レベル22
        assignPlanetIcons(listOf(
            GridItem(id = "target", position = Offset(2f, 2f), length = 2, isHorizontal = false,
                isTarget = true),
            GridItem(id = "v1", position = Offset(0f, 0f), length = 2, isHorizontal = true),
            GridItem(id = "v2", position = Offset(4f, 0f), length = 3, isHorizontal = false),
            GridItem(id = "v3", position = Offset(5f, 0f), length = 2, isHorizontal = false),
            GridItem(id = "v4", position = Offset(0f, 1f), length = 3, isHorizontal = true),
            GridItem(id = "v5", position = Offset(0f, 2f), length = 2, isHorizontal = true),
            GridItem(id = "v6", position = Offset(5f, 2f), length = 2, isHorizontal = false),
            GridItem(id = "v7", position = Offset(0f, 3f), length = 3, isHorizontal = false),
            GridItem(id = "v8", position = Offset(1f, 3f), length = 2, isHorizontal = false),
            GridItem(id = "v9", position = Offset(3f, 3f), length = 2, isHorizontal = true),
            GridItem(id = "v10", position = Offset(5f, 2f), length = 2, isHorizontal = false),
            GridItem(id = "v11", position = Offset(1f, 5f), length = 2, isHorizontal = true),
            GridItem(id = "v12", position = Offset(3f, 4f), length = 2, isHorizontal = false),
            GridItem(id = "v13", position = Offset(4f, 4f), length = 2, isHorizontal = true),
        )),

        // レベル23
        assignPlanetIcons(listOf(
            GridItem(id = "target", position = Offset(2f, 4f), length = 2, isHorizontal = false,
                isTarget = true),
            GridItem(id = "v1", position = Offset(0f, 0f), length = 2, isHorizontal = true),
            GridItem(id = "v2", position = Offset(2f, 0f), length = 3, isHorizontal = true),
            GridItem(id = "v3", position = Offset(4f, 0f), length = 2, isHorizontal = false),
            GridItem(id = "v4", position = Offset(0f, 1f), length = 3, isHorizontal = false),
            GridItem(id = "v5", position = Offset(1f, 1f), length = 2, isHorizontal = false),
            GridItem(id = "v6", position = Offset(2f, 1f), length = 2, isHorizontal = true),
            GridItem(id = "v7", position = Offset(2f, 2f), length = 2, isHorizontal = true),
            GridItem(id = "v8", position = Offset(4f, 2f), length = 2, isHorizontal = true),
            GridItem(id = "v9", position = Offset(1f, 3f), length = 2, isHorizontal = true),
            GridItem(id = "v10", position = Offset(3f, 3f), length = 2, isHorizontal = false),
            GridItem(id = "v11", position = Offset(5f, 3f), length = 3, isHorizontal = false),
            GridItem(id = "v12", position = Offset(0f, 5f), length = 2, isHorizontal = true),
        )),

        // レベル24
        assignPlanetIcons(listOf(
            GridItem(id = "target", position = Offset(2f, 2f), length = 2, isHorizontal = false,
                isTarget = true),
            GridItem(id = "v1", position = Offset(0f, 0f), length = 2, isHorizontal = true),
            GridItem(id = "v2", position = Offset(0f, 1f), length = 3, isHorizontal = true),
            GridItem(id = "v3", position = Offset(0f, 2f), length = 2, isHorizontal = true),
            GridItem(id = "v4", position = Offset(0f, 3f), length = 3, isHorizontal = false),
            GridItem(id = "v5", position = Offset(1f, 3f), length = 2, isHorizontal = false),
            GridItem(id = "v6", position = Offset(1f, 5f), length = 2, isHorizontal = true),
            GridItem(id = "v7", position = Offset(3f, 3f), length = 2, isHorizontal = true),
            GridItem(id = "v8", position = Offset(3f, 4f), length = 2, isHorizontal = false),
            GridItem(id = "v9", position = Offset(4f, 4f), length = 2, isHorizontal = true),
            GridItem(id = "v10", position = Offset(4f, 0f), length = 2, isHorizontal = false),
            GridItem(id = "v11", position = Offset(5f, 0f), length = 2, isHorizontal = false),
            GridItem(id = "v12", position = Offset(5f, 2f), length = 2, isHorizontal = false),
        )),

        // レベル25 まだ
        assignPlanetIcons(listOf(
            GridItem(id = "target", position = Offset(1f, 2f), length = 2, isHorizontal = true,
                isTarget = true),
            GridItem(id = "v1", position = Offset(2f, 0f), length = 2, isHorizontal = false),
            GridItem(id = "v2", position = Offset(3f, 0f), length = 3, isHorizontal = true),
            GridItem(id = "v3", position = Offset(3f, 1f), length = 2, isHorizontal = false),
            GridItem(id = "v4", position = Offset(4f, 1f), length = 2, isHorizontal = true),
            GridItem(id = "v5", position = Offset(4f, 2f), length = 3, isHorizontal = false),
            GridItem(id = "v6", position = Offset(5f, 2f), length = 2, isHorizontal = false),
            GridItem(id = "v7", position = Offset(1f, 3f), length = 2, isHorizontal = false),
            GridItem(id = "v8", position = Offset(0f, 2f), length = 2, isHorizontal = false),
            GridItem(id = "v9", position = Offset(0f, 4f), length = 2, isHorizontal = false),
            GridItem(id = "v10", position = Offset(2f, 4f), length = 2, isHorizontal = true),
            GridItem(id = "v11", position = Offset(1f, 5f), length = 2, isHorizontal = true),
            GridItem(id = "v12", position = Offset(3f, 5f), length = 2, isHorizontal = true),
            GridItem(id = "v13", position = Offset(5f, 4f), length = 2, isHorizontal = false),
        )),

        // レベル26 まだ
        assignPlanetIcons(listOf(
            GridItem(id = "target", position = Offset(1f, 2f), length = 2, isHorizontal = true,
                isTarget = true),
            GridItem(id = "v1", position = Offset(2f, 0f), length = 2, isHorizontal = false),
            GridItem(id = "v2", position = Offset(3f, 0f), length = 3, isHorizontal = true),
            GridItem(id = "v3", position = Offset(3f, 1f), length = 2, isHorizontal = false),
            GridItem(id = "v4", position = Offset(4f, 1f), length = 2, isHorizontal = true),
            GridItem(id = "v5", position = Offset(4f, 2f), length = 3, isHorizontal = false),
            GridItem(id = "v6", position = Offset(5f, 2f), length = 2, isHorizontal = false),
            GridItem(id = "v7", position = Offset(1f, 3f), length = 2, isHorizontal = false),
            GridItem(id = "v8", position = Offset(0f, 2f), length = 2, isHorizontal = false),
            GridItem(id = "v9", position = Offset(0f, 4f), length = 2, isHorizontal = false),
            GridItem(id = "v10", position = Offset(2f, 4f), length = 2, isHorizontal = true),
            GridItem(id = "v11", position = Offset(1f, 5f), length = 2, isHorizontal = true),
            GridItem(id = "v12", position = Offset(3f, 5f), length = 2, isHorizontal = true),
            GridItem(id = "v13", position = Offset(5f, 4f), length = 2, isHorizontal = false),
        )),

        // レベル27 まだ
        assignPlanetIcons(listOf(
            GridItem(id = "target", position = Offset(1f, 2f), length = 2, isHorizontal = true,
                isTarget = true),
            GridItem(id = "v1", position = Offset(2f, 0f), length = 2, isHorizontal = false),
            GridItem(id = "v2", position = Offset(3f, 0f), length = 3, isHorizontal = true),
            GridItem(id = "v3", position = Offset(3f, 1f), length = 2, isHorizontal = false),
            GridItem(id = "v4", position = Offset(4f, 1f), length = 2, isHorizontal = true),
            GridItem(id = "v5", position = Offset(4f, 2f), length = 3, isHorizontal = false),
            GridItem(id = "v6", position = Offset(5f, 2f), length = 2, isHorizontal = false),
            GridItem(id = "v7", position = Offset(1f, 3f), length = 2, isHorizontal = false),
            GridItem(id = "v8", position = Offset(0f, 2f), length = 2, isHorizontal = false),
            GridItem(id = "v9", position = Offset(0f, 4f), length = 2, isHorizontal = false),
            GridItem(id = "v10", position = Offset(2f, 4f), length = 2, isHorizontal = true),
            GridItem(id = "v11", position = Offset(1f, 5f), length = 2, isHorizontal = true),
            GridItem(id = "v12", position = Offset(3f, 5f), length = 2, isHorizontal = true),
            GridItem(id = "v13", position = Offset(5f, 4f), length = 2, isHorizontal = false),
        )),

        // レベル28 まだ
        assignPlanetIcons(listOf(
            GridItem(id = "target", position = Offset(1f, 2f), length = 2, isHorizontal = true,
                isTarget = true),
            GridItem(id = "v1", position = Offset(2f, 0f), length = 2, isHorizontal = false),
            GridItem(id = "v2", position = Offset(3f, 0f), length = 3, isHorizontal = true),
            GridItem(id = "v3", position = Offset(3f, 1f), length = 2, isHorizontal = false),
            GridItem(id = "v4", position = Offset(4f, 1f), length = 2, isHorizontal = true),
            GridItem(id = "v5", position = Offset(4f, 2f), length = 3, isHorizontal = false),
            GridItem(id = "v6", position = Offset(5f, 2f), length = 2, isHorizontal = false),
            GridItem(id = "v7", position = Offset(1f, 3f), length = 2, isHorizontal = false),
            GridItem(id = "v8", position = Offset(0f, 2f), length = 2, isHorizontal = false),
            GridItem(id = "v9", position = Offset(0f, 4f), length = 2, isHorizontal = false),
            GridItem(id = "v10", position = Offset(2f, 4f), length = 2, isHorizontal = true),
            GridItem(id = "v11", position = Offset(1f, 5f), length = 2, isHorizontal = true),
            GridItem(id = "v12", position = Offset(3f, 5f), length = 2, isHorizontal = true),
            GridItem(id = "v13", position = Offset(5f, 4f), length = 2, isHorizontal = false),
        )),

        // レベル29 まだ
        assignPlanetIcons(listOf(
            GridItem(id = "target", position = Offset(1f, 2f), length = 2, isHorizontal = true,
                isTarget = true),
            GridItem(id = "v1", position = Offset(2f, 0f), length = 2, isHorizontal = false),
            GridItem(id = "v2", position = Offset(3f, 0f), length = 3, isHorizontal = true),
            GridItem(id = "v3", position = Offset(3f, 1f), length = 2, isHorizontal = false),
            GridItem(id = "v4", position = Offset(4f, 1f), length = 2, isHorizontal = true),
            GridItem(id = "v5", position = Offset(4f, 2f), length = 3, isHorizontal = false),
            GridItem(id = "v6", position = Offset(5f, 2f), length = 2, isHorizontal = false),
            GridItem(id = "v7", position = Offset(1f, 3f), length = 2, isHorizontal = false),
            GridItem(id = "v8", position = Offset(0f, 2f), length = 2, isHorizontal = false),
            GridItem(id = "v9", position = Offset(0f, 4f), length = 2, isHorizontal = false),
            GridItem(id = "v10", position = Offset(2f, 4f), length = 2, isHorizontal = true),
            GridItem(id = "v11", position = Offset(1f, 5f), length = 2, isHorizontal = true),
            GridItem(id = "v12", position = Offset(3f, 5f), length = 2, isHorizontal = true),
            GridItem(id = "v13", position = Offset(5f, 4f), length = 2, isHorizontal = false),
        )),

        // レベル30 まだ
        assignPlanetIcons(listOf(
            GridItem(id = "target", position = Offset(1f, 2f), length = 2, isHorizontal = true,
                isTarget = true),
            GridItem(id = "v1", position = Offset(2f, 0f), length = 2, isHorizontal = false),
            GridItem(id = "v2", position = Offset(3f, 0f), length = 3, isHorizontal = true),
            GridItem(id = "v3", position = Offset(3f, 1f), length = 2, isHorizontal = false),
            GridItem(id = "v4", position = Offset(4f, 1f), length = 2, isHorizontal = true),
            GridItem(id = "v5", position = Offset(4f, 2f), length = 3, isHorizontal = false),
            GridItem(id = "v6", position = Offset(5f, 2f), length = 2, isHorizontal = false),
            GridItem(id = "v7", position = Offset(1f, 3f), length = 2, isHorizontal = false),
            GridItem(id = "v8", position = Offset(0f, 2f), length = 2, isHorizontal = false),
            GridItem(id = "v9", position = Offset(0f, 4f), length = 2, isHorizontal = false),
            GridItem(id = "v10", position = Offset(2f, 4f), length = 2, isHorizontal = true),
            GridItem(id = "v11", position = Offset(1f, 5f), length = 2, isHorizontal = true),
            GridItem(id = "v12", position = Offset(3f, 5f), length = 2, isHorizontal = true),
            GridItem(id = "v13", position = Offset(5f, 4f), length = 2, isHorizontal = false),
        )),
    )

    fun getLevelCount(): Int = LEVELS.size

    /**
     * 指定されたレベルの車両リストを取得します。
     * プラネットアイコンは取得時に動的に割り当てられます。
     *
     * @param levelIndex 取得したいレベルのインデックス（0から開始）
     * @return 指定されたレベルの車両リスト（プラネットアイコン割り当て済み）
     * @throws IllegalArgumentException レベルインデックスが範囲外の場合
     */
    fun getRandomizedLevel(levelIndex: Int): List<GridItem> {
        // チュートリアルレベルの場合は、そのまま返す（ランダム化しない）
        if (levelIndex == TUTORIAL_LEVEL_INDEX) {
            return TUTORIAL_LEVEL
        }

        // 通常レベルの場合は、インデックスの有効性を確認
        require(levelIndex in LEVELS.indices) {
            "Invalid level index: $levelIndex. Available levels: 0..${LEVELS.size - 1}"
        }

        // 通常レベルのデータを取得
        val levelData = LEVELS[levelIndex]

        // 現状では単純に返すだけですが、将来的にランダム化ロジックを追加する場合に
        // チュートリアルレベルは除外されるようになっています
        return levelData
    }
}