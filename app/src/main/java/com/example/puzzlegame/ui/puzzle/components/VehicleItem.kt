package com.example.puzzlegame.ui.puzzle.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.puzzlegame.domain.Vehicle
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun VehicleItem(
    vehicle: Vehicle,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onMove: (Offset) -> Unit,
    cellSize: Dp,
    ambulanceIcon: Painter,
    planetIcons: List<Painter>,
) {
    var accumulatedOffset by remember { mutableStateOf(Offset.Zero) }
    var dragStartPosition by remember { mutableStateOf(vehicle.position) }
    val coroutineScope = rememberCoroutineScope()

    val planetIcon = remember(vehicle.imageIndex) {
        if (!vehicle.isTarget && vehicle.imageIndex > 0 && vehicle.imageIndex <= planetIcons.size) {
            planetIcons[vehicle.imageIndex - 1]
        } else null
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .offset {
                val baseOffset = Offset(
                    x = vehicle.position.x * cellSize.toPx(),
                    y = vehicle.position.y * cellSize.toPx()
                )
                IntOffset(
                    x = (baseOffset.x + accumulatedOffset.x).roundToInt(),
                    y = (baseOffset.y + accumulatedOffset.y).roundToInt()
                )
            }
            .offset {
                IntOffset(
                    x = accumulatedOffset.x.roundToInt(),
                    y = accumulatedOffset.y.roundToInt()
                )
            }
            .size(
                width = if (vehicle.isHorizontal) cellSize * vehicle.length else cellSize,
                height = if (!vehicle.isHorizontal) cellSize * vehicle.length else cellSize
            )
            .background(
                color = if (vehicle.isTarget) Color.Red else Color.Blue,
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
            .pointerInput(vehicle.isHorizontal) {
                detectDragGestures(
                    onDragStart = {
                        accumulatedOffset = Offset.Zero
                        dragStartPosition = vehicle.position
                    },
                    onDragEnd = {
                        coroutineScope.launch {
                            val cellSizePx = cellSize.toPx()
                            val deltaGridX = (accumulatedOffset.x / cellSizePx).roundToInt()
                            val deltaGridY = (accumulatedOffset.y / cellSizePx).roundToInt()
                            val newGridPosition = Offset(
                                x = dragStartPosition.x + deltaGridX,
                                y = dragStartPosition.y + deltaGridY
                            )
                            onMove(newGridPosition)
                            accumulatedOffset = Offset.Zero
                        }
                    },
                    onDragCancel = {

                        coroutineScope.launch {
                            accumulatedOffset = Offset.Zero
                        }
                    }
                ) { change, dragAmount ->
                    change.consume()
                    // VehicleItem.ktの変更部分
                    val constrainedDragAmount = if (vehicle.isTarget) {
                        // ターゲット車両は垂直方向にのみ移動可能
                        Offset(0f, dragAmount.y)
                    } else if (vehicle.isHorizontal) {
                        // 通常の水平方向の車両は水平方向にのみ移動可能
                        Offset(dragAmount.x, 0f)
                    } else {
                        // 通常の垂直方向の車両は垂直方向にのみ移動可能
                        Offset(0f, dragAmount.y)
                    }
                    val newAccumulatedOffset = Offset(
                        x = accumulatedOffset.x + constrainedDragAmount.x,
                        y = accumulatedOffset.y + constrainedDragAmount.y
                    )
                    val cellSizePx = cellSize.toPx()
                    val maxOffset = cellSizePx * 2
                    accumulatedOffset = newAccumulatedOffset.copy(
                        x = newAccumulatedOffset.x.coerceIn(-maxOffset, maxOffset),
                        y = newAccumulatedOffset.y.coerceIn(-maxOffset, maxOffset)
                    )
                }
            }
    ) {
        when {
            vehicle.isTarget -> {
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
                    contentDescription = "Planet ${vehicle.imageIndex}"
                )
            }
        }
    }
}