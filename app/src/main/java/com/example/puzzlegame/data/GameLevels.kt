package com.example.puzzlegame.data

import androidx.compose.ui.geometry.Offset
import com.example.puzzlegame.domain.Vehicle

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
                position = Offset(2f, 0f),
                length = 2,
                isHorizontal = false,
            ),
            Vehicle(
                id = "v6",
                position = Offset(4f, 2f),
                length = 3,
                isHorizontal = false,
            ),
            Vehicle(
                id = "v7",
                position = Offset(5f, 2f),
                length = 2,
                isHorizontal = false,
            ),
            Vehicle(
                id = "v8",
                position = Offset(1f, 3f),
                length = 2,
                isHorizontal = false,
            ),
            Vehicle(
                id = "v9",
                position = Offset(0f, 2f),
                length = 2,
                isHorizontal = false,
            ),
            Vehicle(
                id = "v10",
                position = Offset(0f, 4f),
                length = 2,
                isHorizontal = false,
            ),
            Vehicle(
                id = "v11",
                position = Offset(2f, 4f),
                length = 2,
                isHorizontal = true,
            ),
            Vehicle(
                id = "v12",
                position = Offset(1f, 5f),
                length = 2,
                isHorizontal = true,
            ),
            Vehicle(
                id = "v13",
                position = Offset(3f, 5f),
                length = 2,
                isHorizontal = true,
            ),
            Vehicle(
                id = "v14",
                position = Offset(5f, 4f),
                length = 2,
                isHorizontal = false,
            ),
        )
    )
}