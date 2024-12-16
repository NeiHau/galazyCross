package com.example.puzzlegame.ui.puzzle.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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
    cellSize: Dp
) {
    // ドラッグ開始位置からの累積オフセットを追跡
    var accumulatedOffset by remember { mutableStateOf(Offset.Zero) }
    // ドラッグ開始時の位置を記録
    var dragStartPosition by remember { mutableStateOf(vehicle.position) }

    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .offset {
                // 現在のグリッド位置 + 累積オフセットを計算
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
                // ドラッグ中の一時的なオフセットを適用
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
                        // ドラッグ開始時の状態をリセット
                        accumulatedOffset = Offset.Zero
                        dragStartPosition = vehicle.position
                    },
                    onDragEnd = {
                        coroutineScope.launch {
                            val cellSizePx = cellSize.toPx()

                            // 累積オフセットをグリッドセル単位に変換
                            val deltaGridX = (accumulatedOffset.x / cellSizePx).roundToInt()
                            val deltaGridY = (accumulatedOffset.y / cellSizePx).roundToInt()

                            // 開始位置からの相対的な新しいグリッド位置を計算
                            val newGridPosition = Offset(
                                x = dragStartPosition.x + deltaGridX,
                                y = dragStartPosition.y + deltaGridY
                            )

                            // ViewModelに新しい位置を通知
                            onMove(newGridPosition)

                            // オフセットをリセット
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

                    // ドラッグ量を車両の向きに応じて制限
                    val constrainedDragAmount = if (vehicle.isHorizontal) {
                        Offset(dragAmount.x, 0f)
                    } else {
                        Offset(0f, dragAmount.y)
                    }

                    // 累積オフセットを更新
                    val newAccumulatedOffset = Offset(
                        x = accumulatedOffset.x + constrainedDragAmount.x,
                        y = accumulatedOffset.y + constrainedDragAmount.y
                    )

                    // グリッドの範囲内に制限
                    val cellSizePx = cellSize.toPx()
                    val maxOffset = cellSizePx * 2 // 最大2マスまでの移動を許可

                    accumulatedOffset = newAccumulatedOffset.copy(
                        x = newAccumulatedOffset.x.coerceIn(-maxOffset, maxOffset),
                        y = newAccumulatedOffset.y.coerceIn(-maxOffset, maxOffset)
                    )
                }
            }
    )
}