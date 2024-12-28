package com.example.puzzlegame.ui.puzzle

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.puzzlegame.data.GameLevels.LEVELS
import com.example.puzzlegame.ui.puzzle.components.GameClearDialog
import com.example.puzzlegame.ui.puzzle.components.VehicleControl
import com.example.puzzlegame.ui.puzzle.components.VehicleItem
import com.example.rushgame.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuzzleScreen(
    levelIndex: Int,
    onNavigateToLevel: (Int) -> Unit,
    onBackToLevelSelection: () -> Unit,
) {
    val rushHourViewModel: RushHourViewModel = viewModel()
    val gameState by rushHourViewModel.gameState.collectAsState()
    val focusManager = LocalFocusManager.current
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(gameState.isGameComplete) {
        if (gameState.isGameComplete) {
            showDialog = true
        }
    }

    LaunchedEffect(levelIndex) {
        rushHourViewModel.initializeGame(levelIndex)
    }

    Scaffold(
        modifier = Modifier.zIndex(1f),
        topBar = {
            TopAppBar(
                title = { Text("レベル ${levelIndex + 1}") },
                navigationIcon = {
                    IconButton(
                        onClick = onBackToLevelSelection,
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "戻る"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (showDialog && gameState.isGameComplete) {
            GameClearDialog(
                currentLevel = levelIndex,
                hasNextLevel = levelIndex < LEVELS.size - 1,
                onReplay = {
                    showDialog = false
                    rushHourViewModel.initializeGame(levelIndex)
                },
                onNextLevel = {
                    showDialog = false
                    onNavigateToLevel(levelIndex + 1)
                },
                onShowLevelSelection = {
                    showDialog = false
                    focusManager.clearFocus()
                    onBackToLevelSelection()
                }
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_puzzle_game_background_image),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val boardSize = LocalConfiguration.current.screenWidthDp.dp - 32.dp

            TextButton(
                modifier = Modifier
                    .width(width = 120.dp)
                    .padding(vertical = 12.dp)
                    .clip(shape = RoundedCornerShape(size = 16.dp))
                    .background(color = Color.White),
                content = {
                    Text(
                        text =  "リセット",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                    )
                },
                onClick = { rushHourViewModel.initializeGame(levelIndex) },
                enabled =!gameState.isGameComplete,
            )
            Spacer(modifier = Modifier.height(height = 12.dp))
            Box(
                modifier = Modifier.size(boardSize)
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
                cellSize = boardSize / 6,
            )
        }
    }
}

@Composable
private fun GridBackground(boardSize: Dp) {
    Box(modifier = Modifier.size(boardSize)) {
        Box(
            modifier = Modifier
                .size(boardSize)
                .background(Color(0xFFC5C5C5))
        )

        // グリッド線
        Canvas(modifier = Modifier.size(boardSize)) {
            val cellSize = size.width / 6f
            val strokeWidth = 1.dp.toPx()

            // 縦線
            for (i in 0..6) {
                val x = i * cellSize
                drawLine(
                    color = Color.Black.copy(alpha = 0.8f),
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = strokeWidth
                )
            }

            // 横線
            for (i in 0..6) {
                val y = i * cellSize
                drawLine(
                    color = Color.Black.copy(alpha = 0.8f),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = strokeWidth
                )
            }
        }
    }
}