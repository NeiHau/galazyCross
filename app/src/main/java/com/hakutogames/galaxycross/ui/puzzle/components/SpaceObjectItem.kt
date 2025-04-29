package com.hakutogames.galaxycross.ui.puzzle.components

import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
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
import com.hakutogames.galaxycross.domain.GridItem
import kotlin.math.roundToInt

@Composable
fun SpaceObjectItem(
    gridItem: GridItem,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onMove: (Offset) -> Unit,
    cellSize: Dp,
    ambulanceIcon: Painter,
    planetIcons: List<Painter>,
) {
    val planetIcon = remember(gridItem.imageIndex) {
        if (!gridItem.isTarget && gridItem.imageIndex > 0 && gridItem.imageIndex <= planetIcons.size) {
            planetIcons[gridItem.imageIndex - 1]
        } else {
            null
        }
    }

    // ドラッグ中かどうかの状態を管理
    var isDragging by remember { mutableStateOf(false) }

    // 前回の移動時間を記憶
    var lastMoveTime by remember { mutableStateOf(0L) }

    // ドラッグの累積量を追跡
    var accumulatedDragX by remember { mutableStateOf(0f) }
    var accumulatedDragY by remember { mutableStateOf(0f) }

    // 現在のマス目位置を記憶
    var currentGridX by remember { mutableStateOf(gridItem.position.x) }
    var currentGridY by remember { mutableStateOf(gridItem.position.y) }

    // 前回の移動方向を記憶
    var lastHorizontalDirection by remember { mutableStateOf(0) } // -1: 左、0: 初期状態、1: 右
    var lastVerticalDirection by remember { mutableStateOf(0) } // -1: 上、0: 初期状態、1: 下

    // ドラッグ中の視覚的フィードバック用の色
    val itemColor = when {
        gridItem.isTarget -> Color.Red
        isDragging -> Color(0xFF5599FF) // ドラッグ中は明るい青に変える
        isSelected -> Color(0xFF3377DD) // 選択中は濃い青
        else -> Color.Blue
    }

    // ドラッグ中のボーダーの色と太さ
    val borderWidth = if (isDragging) {
        3.dp // ドラッグ中は太く
    } else if (isSelected) {
        2.dp // 選択中
    } else {
        0.dp // 非選択時
    }
    val borderColor = if (isDragging) Color(0xFFFFFF00) else Color.Yellow

    // アニメーションを適用した位置
    val animatedPosition by animateOffsetAsState(
        targetValue = gridItem.position,
        animationSpec = tween(durationMillis = 50), // 50msのアニメーション（より高速）
        label = "position",
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .offset {
                IntOffset(
                    x = (animatedPosition.x * cellSize.toPx()).roundToInt(),
                    y = (animatedPosition.y * cellSize.toPx()).roundToInt(),
                )
            }
            .size(
                width = if (gridItem.isHorizontal) cellSize * gridItem.length else cellSize,
                height = if (!gridItem.isHorizontal) cellSize * gridItem.length else cellSize,
            )
            .background(
                color = itemColor,
                shape = RoundedCornerShape(4.dp),
            )
            .border(
                width = borderWidth,
                color = borderColor,
                shape = RoundedCornerShape(4.dp),
            )
            // ドラッグ検出（選択状態に関係なく有効）
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        isDragging = true
                        // 移動方向をリセット
                        lastHorizontalDirection = 0
                        lastVerticalDirection = 0
                        // 最後の移動時間をリセット
                        lastMoveTime = 0L
                        // 累積量をリセット
                        accumulatedDragX = 0f
                        accumulatedDragY = 0f
                        // 現在のマス目位置を更新
                        currentGridX = gridItem.position.x
                        currentGridY = gridItem.position.y
                    },
                    onDragEnd = {
                        isDragging = false
                        // 累積量をリセット
                        accumulatedDragX = 0f
                        accumulatedDragY = 0f
                    },
                    onDragCancel = {
                        isDragging = false
                        // 累積量をリセット
                        accumulatedDragX = 0f
                        accumulatedDragY = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()

                        // 現在の時間を取得
                        val currentTime = System.currentTimeMillis()

                        // セルサイズを取得
                        val cellSizePx = cellSize.toPx()
                        val thresholdPx = cellSizePx * 0.08f // セルサイズの8%を閾値とする（より感度を高める）

                        // ドラッグ量を累積
                        accumulatedDragX += dragAmount.x
                        accumulatedDragY += dragAmount.y

                        // 移動のクールダウン時間（連続移動を防止するため）
                        val cooldownMs = 50L // より短いクールダウン時間で連続移動を容易に

                        // 水平方向のドラッグを処理
                        if (gridItem.isHorizontal) {
                            // 累積ドラッグ量から移動すべきマス数を計算
                            val cellsToMoveX = (accumulatedDragX / cellSizePx).toInt()

                            // 移動すべきマス数がある場合
                            if (cellsToMoveX != 0) {
                                // 移動方向を計算
                                val direction = if (cellsToMoveX > 0) 1 else -1

                                // 前回の移動から十分な時間が経過しているか、方向が変わった場合に移動
                                if (currentTime - lastMoveTime > cooldownMs || direction != lastHorizontalDirection) {
                                    // 新しいマス目位置を計算
                                    val newGridX = currentGridX + direction

                                    // 移動実行
                                    val newPosition = Offset(newGridX, gridItem.position.y)
                                    onMove(newPosition)

                                    // 移動後の状態を更新
                                    currentGridX = newGridX
                                    lastMoveTime = currentTime
                                    lastHorizontalDirection = direction

                                    // 累積ドラッグ量をリセット（移動分を差し引く）
                                    accumulatedDragX -= direction * cellSizePx
                                }
                            }
                        }
                        // 垂直方向のドラッグを処理
                        else {
                            // 累積ドラッグ量から移動すべきマス数を計算
                            val cellsToMoveY = (accumulatedDragY / cellSizePx).toInt()

                            // 移動すべきマス数がある場合
                            if (cellsToMoveY != 0) {
                                // 移動方向を計算
                                val direction = if (cellsToMoveY > 0) 1 else -1

                                // 前回の移動から十分な時間が経過しているか、方向が変わった場合に移動
                                if (currentTime - lastMoveTime > cooldownMs || direction != lastVerticalDirection) {
                                    // 新しいマス目位置を計算
                                    val newGridY = currentGridY + direction

                                    // 移動実行
                                    val newPosition = Offset(gridItem.position.x, newGridY)
                                    onMove(newPosition)

                                    // 移動後の状態を更新
                                    currentGridY = newGridY
                                    lastMoveTime = currentTime
                                    lastVerticalDirection = direction

                                    // 累積ドラッグ量をリセット（移動分を差し引く）
                                    accumulatedDragY -= direction * cellSizePx
                                }
                            }
                        }
                    },
                )
                // タップしたアイテムを選択状態にする
                onSelect()
            },
    ) {
        when {
            gridItem.isTarget -> {
                Image(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    contentScale = ContentScale.FillBounds,
                    painter = ambulanceIcon,
                    contentDescription = "Target Vehicle",
                )
            }
            planetIcon != null -> {
                Image(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    contentScale = ContentScale.Fit,
                    painter = planetIcon,
                    contentDescription = "Planet ${gridItem.imageIndex}",
                )
            }
        }
    }
}
