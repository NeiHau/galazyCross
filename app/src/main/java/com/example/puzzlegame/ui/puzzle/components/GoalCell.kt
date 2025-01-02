package com.example.puzzlegame.ui.puzzle.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun GoalCell(
    cellSize: Dp
) {
    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    x = (2 * cellSize.toPx()).roundToInt(),
                    y = (-cellSize.toPx()).roundToInt()
                )
            }
            .size(cellSize)
            .background(
                color = Color.Red,
                shape = RoundedCornerShape(4.dp)
            )
            .border(
                width = 1.dp,
                color = Color.Green,
                shape = RoundedCornerShape(4.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "ゴール",
            color = Color.Yellow,
            fontWeight = FontWeight.Bold,
        )
    }
}