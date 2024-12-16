package com.example.puzzlegame.ui.puzzle

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.puzzlegame.data.GameLevels.LEVELS
import com.example.puzzlegame.ui.puzzle.components.GameClearDialog
import com.example.puzzlegame.ui.puzzle.components.VehicleControl
import com.example.puzzlegame.ui.puzzle.components.VehicleItem

@Composable
fun PuzzleScreen(
    levelIndex: Int,
    onNavigateToLevel: (Int) -> Unit,
    onBackToLevelSelection: () -> Unit
) {
    val rushHourViewModel: RushHourViewModel = viewModel()
    val gameState by rushHourViewModel.gameState.collectAsState()

    // 初期化時に正しいレベルを設定
    LaunchedEffect(levelIndex) {
        rushHourViewModel.initializeGame(levelIndex)
    }

    // ゲームクリア時のダイアログ表示
    if (gameState.isGameComplete) {
        GameClearDialog(
            currentLevel = levelIndex,
            hasNextLevel = levelIndex < LEVELS.size - 1,
            onReplay = { rushHourViewModel.initializeGame(levelIndex) },
            onNextLevel = { onNavigateToLevel(levelIndex + 1) },
            onShowLevelSelection = onBackToLevelSelection
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 戻るボタンを追加
        IconButton(
            onClick = onBackToLevelSelection,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "戻る")
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 現在のレベルを表示
            Text(
                text = "レベル ${levelIndex + 1}",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 既存のゲームボードの実装
            val boardSize = LocalConfiguration.current.screenWidthDp.dp - 32.dp
            Box(
                modifier = Modifier
                    .size(boardSize)
                    .padding(4.dp)
            ) {
                GridBackground(boardSize)

                gameState.vehicles.forEach { vehicle ->
                    VehicleItem(
                        vehicle = vehicle,
                        isSelected = vehicle.id == gameState.selectedVehicleId,
                        onSelect = { rushHourViewModel.selectVehicle(vehicle.id) },
                        onMove = { offset ->
                            rushHourViewModel.moveVehicle(vehicle.id, offset)
                        },
                        cellSize = boardSize / 6
                    )
                }
            }

            Spacer(modifier = Modifier.height(66.dp))

            VehicleControl(
                vehicle = gameState.selectedVehicleId?.let { selectedId ->
                    gameState.vehicles.find { it.id == selectedId }
                },
                onMove = { offset ->
                    gameState.selectedVehicleId?.let { selectedId ->
                        rushHourViewModel.moveVehicle(selectedId, offset)
                    }
                },
                cellSize = boardSize / 6
            )
        }
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
