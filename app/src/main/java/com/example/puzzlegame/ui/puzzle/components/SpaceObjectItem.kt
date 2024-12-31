package com.example.puzzlegame.ui.puzzle.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.puzzlegame.domain.GridItem
import kotlin.math.roundToInt

@Composable
fun SpaceObjectItem(
    gridItem: GridItem,
    isSelected: Boolean,
    onSelect: () -> Unit,
    cellSize: Dp,
    ambulanceIcon: Painter,
    planetIcons: List<Painter>,
) {
    val planetIcon = remember(gridItem.imageIndex) {
        if (!gridItem.isTarget && gridItem.imageIndex > 0 && gridItem.imageIndex <= planetIcons.size) {
            planetIcons[gridItem.imageIndex - 1]
        } else null
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .offset {
                IntOffset(
                    x = (gridItem.position.x * cellSize.toPx()).roundToInt(),
                    y = (gridItem.position.y * cellSize.toPx()).roundToInt()
                )
            }
            .size(
                width = if (gridItem.isHorizontal) cellSize * gridItem.length else cellSize,
                height = if (!gridItem.isHorizontal) cellSize * gridItem.length else cellSize
            )
            .background(
                color = if (gridItem.isTarget) Color.Red else Color.Blue,
                shape = RoundedCornerShape(4.dp)
            )
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = Color.Yellow,
                shape = RoundedCornerShape(4.dp)
            )
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onSelect() })
            }
    ) {
        when {
            gridItem.isTarget -> {
                Image(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    contentScale = ContentScale.FillBounds,
                    painter = ambulanceIcon,
                    contentDescription = "Target Vehicle"
                )
            }
            planetIcon != null -> {
                Image(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    contentScale = ContentScale.Fit,
                    painter = planetIcon,
                    contentDescription = "Planet ${gridItem.imageIndex}"
                )
            }
        }
    }
}