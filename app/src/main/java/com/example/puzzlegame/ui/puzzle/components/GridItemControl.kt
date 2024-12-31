package com.example.puzzlegame.ui.puzzle.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.puzzlegame.domain.GridItem
import com.example.rushgame.R

@Composable
fun GridItemControl(
    gridItem: GridItem?,
    onMove: (Offset) -> Unit,
) {
    val buttonSize = 48.dp
    val inactiveIconAlpha = 0.3f

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // 上方向のボタン
        IconButton(
            modifier = Modifier.size(buttonSize),
            onClick = {
                gridItem?.let { v ->
                    val newPosition = Offset(v.position.x, v.position.y - 1)
                    onMove(newPosition)
                }
            },
            enabled = gridItem?.let { !it.isHorizontal } ?: false,
        ) {
            Image(
                modifier = Modifier.size(60.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_round_arrow_up_icon),
                contentDescription = "上へ移動",
                colorFilter = ColorFilter.tint(
                    color = when {
                        gridItem == null -> Color.White.copy(alpha = inactiveIconAlpha)
                        !gridItem.isHorizontal -> Color.White
                        else -> Color.White.copy(alpha = inactiveIconAlpha)
                    }
                )
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(64.dp)
        ) {
            // 左方向のボタン
            IconButton(
                modifier = Modifier.size(buttonSize),
                onClick = {
                    gridItem?.let { v ->
                        val newPosition = Offset(v.position.x - 1, v.position.y)
                        onMove(newPosition)
                    }
                },
                enabled = gridItem?.let { it.isHorizontal } ?: false,
            ) {
                Image(
                    modifier = Modifier.size(60.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.ic_round_arrow_left_icon),
                    contentDescription = "左へ移動",
                    colorFilter = ColorFilter.tint(
                        color = when {
                            gridItem == null -> Color.White.copy(alpha = inactiveIconAlpha)
                            gridItem.isHorizontal -> Color.White
                            else -> Color.White.copy(alpha = inactiveIconAlpha)
                        }
                    )
                )
            }

            // 右方向のボタン
            IconButton(
                modifier = Modifier.size(buttonSize),
                onClick = {
                    gridItem?.let { v ->
                        val newPosition = Offset(v.position.x + 1, v.position.y)
                        onMove(newPosition)
                    }
                },
                enabled = gridItem?.isHorizontal ?: false,
            ) {
                Image(
                    modifier = Modifier.size(60.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.ic_round_arrow_right_icon),
                    contentDescription = "右へ移動",
                    colorFilter = ColorFilter.tint(
                        color = when {
                            gridItem == null -> Color.White.copy(alpha = inactiveIconAlpha)
                            gridItem.isHorizontal -> Color.White
                            else -> Color.White.copy(alpha = inactiveIconAlpha)
                        }
                    )
                )
            }
        }

        // 下方向のボタン
        IconButton(
            modifier = Modifier.size(buttonSize),
            onClick = {
                gridItem?.let { v ->
                    val newPosition = Offset(v.position.x, v.position.y + 1)
                    onMove(newPosition)
                }
            },
            enabled = gridItem?.let { !it.isHorizontal } ?: false,
        ) {
            Image(
                modifier = Modifier.size(60.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_round_arrow_down_icon),
                contentDescription = "下へ移動",
                colorFilter = ColorFilter.tint(
                    color = when {
                        gridItem == null -> Color.White.copy(alpha = inactiveIconAlpha)
                        !gridItem.isHorizontal -> Color.White
                        else -> Color.White.copy(alpha = inactiveIconAlpha)
                    }
                )
            )
        }
    }
}