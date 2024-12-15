package com.example.rushgame.ui.rushhour

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun RushHourScreen() {
    val rushHourViewModel: RushHourViewModel = viewModel()
    val gameState by rushHourViewModel.gameState.collectAsState()

    // ゲームクリア時のダイアログ表示
    if (gameState.isGameComplete) {
        GameClearDialog(onReplay = { rushHourViewModel.initializeGame(4) })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ゲームボード全体を正方形に保つ
        val boardSize = LocalConfiguration.current.screenWidthDp.dp - 32.dp // 画面幅から padding を引く
        Box(
            modifier = Modifier
                .size(boardSize) // 正方形のボードを作成
                .background(Color.LightGray)
                .padding(4.dp)
        ) {
            GridBackground(boardSize)

            gameState.vehicles.forEach { vehicle ->
                VehicleItem(
                    vehicle = vehicle,
                    isSelected = vehicle.id == gameState.selectedVehicleId,
                    onSelect = { rushHourViewModel.selectVehicle(vehicle.id) },
                    onMove = { offset -> rushHourViewModel.moveVehicle(vehicle.id, offset) },
                    cellSize = boardSize / 6 // セルサイズを6分割に調整
                )
            }
        }

        Spacer(modifier = Modifier.height(66.dp))

        VehicleControl(
            vehicle = gameState.selectedVehicleId?.let { selectedId ->
                gameState.vehicles.find { it.id == selectedId }
            }, // 選択された車両を渡す (ない場合は null)
            onMove = { offset ->
                gameState.selectedVehicleId?.let { selectedId ->
                    rushHourViewModel.moveVehicle(selectedId, offset)
                }
            },
            cellSize = boardSize / 6
        )
    }
}

@Composable
private fun GridBackground(boardSize: Dp) {
    val cellSize = boardSize / 6 // ボードサイズを6等分

    // 6×6のグリッドを作成
    repeat(6) { row ->
        repeat(6) { col ->
            Box(
                modifier = Modifier
                    .offset(
                        x = col * cellSize,
                        y = row * cellSize
                    )
                    .size(cellSize)
                    .border(0.5.dp, Color.Gray)
                    .background(Color.White)
            )
        }
    }
}

@Composable
private fun VehicleItem(
    vehicle: RushHourViewModel.Vehicle,
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

@Composable
fun VehicleControl(
    vehicle: RushHourViewModel.Vehicle?,
    onMove: (Offset) -> Unit,
    cellSize: Dp
) {
    val buttonSize = 48.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // 上ボタン
        Button(
            onClick = {
                vehicle?.let {
                    val newPosition = Offset(it.position.x, it.position.y - 1)
                    onMove(newPosition)
                }
            },
            modifier = Modifier.size(buttonSize),
            enabled = vehicle != null // 選択車両がない場合は無効化
        ) {
            Text("↑")
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 左ボタン
            Button(
                onClick = {
                    vehicle?.let {
                        val newPosition = Offset(it.position.x - 1, it.position.y)
                        onMove(newPosition)
                    }
                },
                modifier = Modifier.size(buttonSize),
                enabled = vehicle != null
            ) {
                Text("←")
            }
            // 右ボタン
            Button(
                onClick = {
                    vehicle?.let {
                        val newPosition = Offset(it.position.x + 1, it.position.y)
                        onMove(newPosition)
                    }
                },
                modifier = Modifier.size(buttonSize),
                enabled = vehicle != null
            ) {
                Text("→")
            }
        }
        // 下ボタン
        Button(
            onClick = {
                vehicle?.let {
                    val newPosition = Offset(it.position.x, it.position.y + 1)
                    onMove(newPosition)
                }
            },
            modifier = Modifier.size(buttonSize),
            enabled = vehicle != null
        ) {
            Text("↓")
        }
    }
}

@Composable
fun GameClearDialog(onReplay: () -> Unit) {
    AlertDialog(
        onDismissRequest = { /* ダイアログを閉じる処理は不要 */ },
        title = {
            Text(text = "ゲームクリア！", style = MaterialTheme.typography.bodyLarge)
        },
        text = {
            Text(text = "赤い車を出口まで移動させました！")
        },
        confirmButton = {
            Button(onClick = onReplay) {
                Text("もう一度プレイ")
            }
        }
    )
}


