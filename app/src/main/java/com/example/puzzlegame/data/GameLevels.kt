package com.example.puzzlegame.data

import androidx.compose.ui.geometry.Offset
import com.example.puzzlegame.domain.Vehicle

object GameLevels {
    // ゲームの全レベルを格納する定数
    val LEVELS = listOf(
        // レベル1: 初心者向けの簡単な配置
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
                position = Offset(3f, 2f),
                length = 2,
                isHorizontal = true
            ),
            Vehicle(
                id = "v2",
                position = Offset(2f, 0f),
                length = 2,
                isHorizontal = false
            )
        ),

        // レベル2: やや複雑な配置
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
                position = Offset(0f, 0f),
                length = 2,
                isHorizontal = false
            ),
            Vehicle(
                id = "v2",
                position = Offset(3f, 1f),
                length = 2,
                isHorizontal = true
            ),
            Vehicle(
                id = "v3",
                position = Offset(3f, 2f),
                length = 3,
                isHorizontal = false
            )
        ),

        // レベル3: 中級者向けの配置
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
                position = Offset(0f, 0f),
                length = 3,
                isHorizontal = false
            ),
            Vehicle(
                id = "v2",
                position = Offset(1f, 0f),
                length = 2,
                isHorizontal = true
            ),
            Vehicle(
                id = "v3",
                position = Offset(3f, 0f),
                length = 3,
                isHorizontal = false
            ),
            Vehicle(
                id = "v4",
                position = Offset(4f, 1f),
                length = 2,
                isHorizontal = true
            )
        ),

        // レベル4: より複雑な配置
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
                position = Offset(0f, 0f),
                length = 3,
                isHorizontal = true
            ),
            Vehicle(
                id = "v2",
                position = Offset(0f, 1f),
                length = 2,
                isHorizontal = true
            ),
            Vehicle(
                id = "v3",
                position = Offset(3f, 0f),
                length = 2,
                isHorizontal = false
            ),
            Vehicle(
                id = "v4",
                position = Offset(4f, 0f),
                length = 2,
                isHorizontal = false
            ),
            Vehicle(
                id = "v5",
                position = Offset(3f, 3f),
                length = 3,
                isHorizontal = true
            )
        ),

        // レベル5: 上級者向けの配置
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
                position = Offset(0f, 0f),
                length = 3,
                isHorizontal = false
            ),
            Vehicle(
                id = "v2",
                position = Offset(1f, 0f),
                length = 2,
                isHorizontal = true
            ),
            Vehicle(
                id = "v3",
                position = Offset(3f, 0f),
                length = 3,
                isHorizontal = false
            ),
            Vehicle(
                id = "v4",
                position = Offset(4f, 1f),
                length = 2,
                isHorizontal = true
            ),
            Vehicle(
                id = "v5",
                position = Offset(0f, 3f),
                length = 2,
                isHorizontal = true
            ),
            Vehicle(
                id = "v6",
                position = Offset(3f, 4f),
                length = 3,
                isHorizontal = true
            )
        )
    )
}