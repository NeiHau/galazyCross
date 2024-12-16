package com.example.puzzlegame.ui.puzzle.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.puzzlegame.domain.Vehicle

@Composable
fun VehicleControl(
    vehicle: Vehicle?,
    onMove: (Offset) -> Unit,
    cellSize: Dp
) {
    // ボタンのサイズを定数として定義
    val buttonSize = 48.dp

    // 選択された車両がない場合は何も表示しない
    if (vehicle == null) return

    // アイコンのスタイリングのための定数を定義
    val activeIconAlpha = 1f       // アクティブな方向のアイコンの不透明度
    val inactiveIconAlpha = 0.3f   // 非アクティブな方向のアイコンの不透明度

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // 上方向のボタン
        Button(
            onClick = {
                val newPosition = Offset(vehicle.position.x, vehicle.position.y - 1)
                onMove(newPosition)
            },
            modifier = Modifier.size(buttonSize),
            enabled = !vehicle.isHorizontal
        ) {
            // 縦向きの車両の場合は濃く、横向きの場合は薄く表示
            Text(
                text = "↑",
                modifier = Modifier.graphicsLayer(
                    alpha = if (!vehicle.isHorizontal) activeIconAlpha else inactiveIconAlpha
                )
            )
        }

        // 左右のボタンを含む行
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 左方向のボタン
            Button(
                onClick = {
                    val newPosition = Offset(vehicle.position.x - 1, vehicle.position.y)
                    onMove(newPosition)
                },
                modifier = Modifier.size(buttonSize),
                enabled = vehicle.isHorizontal
            ) {
                // 横向きの車両の場合は濃く、縦向きの場合は薄く表示
                Text(
                    text = "←",
                    modifier = Modifier.graphicsLayer(
                        alpha = if (vehicle.isHorizontal) activeIconAlpha else inactiveIconAlpha
                    )
                )
            }

            // 右方向のボタン
            Button(
                onClick = {
                    val newPosition = Offset(vehicle.position.x + 1, vehicle.position.y)
                    onMove(newPosition)
                },
                modifier = Modifier.size(buttonSize),
                enabled = vehicle.isHorizontal
            ) {
                // 横向きの車両の場合は濃く、縦向きの場合は薄く表示
                Text(
                    text = "→",
                    modifier = Modifier.graphicsLayer(
                        alpha = if (vehicle.isHorizontal) activeIconAlpha else inactiveIconAlpha
                    )
                )
            }
        }

        // 下方向のボタン
        Button(
            onClick = {
                val newPosition = Offset(vehicle.position.x, vehicle.position.y + 1)
                onMove(newPosition)
            },
            modifier = Modifier.size(buttonSize),
            enabled = !vehicle.isHorizontal
        ) {
            // 縦向きの車両の場合は濃く、横向きの場合は薄く表示
            Text(
                text = "↓",
                modifier = Modifier.graphicsLayer(
                    alpha = if (!vehicle.isHorizontal) activeIconAlpha else inactiveIconAlpha
                )
            )
        }
    }
}