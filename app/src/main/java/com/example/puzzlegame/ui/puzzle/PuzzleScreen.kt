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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.galaxycross.R
import com.example.puzzlegame.data.GameLevels
import com.example.puzzlegame.data.GameLevels.LEVELS
import com.example.puzzlegame.domain.GameState
import com.example.puzzlegame.ui.common.dialog.GameClearDialog
import com.example.puzzlegame.ui.common.dialog.TutorialDialog
import com.example.puzzlegame.ui.puzzle.components.GoalCell
import com.example.puzzlegame.ui.puzzle.components.GridItemControl
import com.example.puzzlegame.ui.puzzle.components.SpaceObjectItem
import com.example.puzzlegame.ui.puzzle.components.rememberPlanetIcons

const val TUTORIAL_LEVEL_INDEX = -1

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuzzleScreen(
    puzzleViewModel: PuzzleViewModel = hiltViewModel(),
    isTutorialCompleted: Boolean,
    levelIndex: Int,
    onLevelCleared: (Int) -> Unit,
    onNavigateToLevel: (Int) -> Unit,
    onBackToLevelSelection: () -> Unit,
) {
    val gameState by puzzleViewModel.gameState.collectAsState()
    val focusManager = LocalFocusManager.current
    var showDialog by remember { mutableStateOf(false) }

    // リソース
    val spaceShuttleIcon = painterResource(id = R.drawable.ic_space_shuttle)
    val planetIcons = rememberPlanetIcons()
    val boardSize = LocalConfiguration.current.screenWidthDp.dp - 56.dp

    // チュートリアルダイアログの表示条件
    val showTutorialDialog = levelIndex == GameLevels.TUTORIAL_LEVEL_INDEX && !isTutorialCompleted
    var showTutorial by remember { mutableStateOf(showTutorialDialog) }

    LaunchedEffect(levelIndex) {
        puzzleViewModel.initializeGame(levelIndex)
    }

    LaunchedEffect(gameState.isGameComplete) {
        if (gameState.isGameComplete) {
            showDialog = true
            onLevelCleared(levelIndex)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.zIndex(1f),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = if (levelIndex == TUTORIAL_LEVEL_INDEX) "チュートリアル" else "レベル ${levelIndex + 1}",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackToLevelSelection) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "戻る",
                            )
                        }
                    },
                )
            }
        ) { paddingValues ->
            if (showDialog && gameState.isGameComplete) {
                GameClearDialog(
                    currentLevel = levelIndex,
                    hasNextLevel = levelIndex < LEVELS.size - 1,
                    onReplay = {
                        showDialog = false
                        puzzleViewModel.initializeGame(levelIndex)
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
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(id = R.drawable.universe_space01),
                    contentDescription = null,
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
                TextButton(
                    modifier = Modifier
                        .width(120.dp)
                        .padding(vertical = 12.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White),
                    onClick = { puzzleViewModel.initializeGame(levelIndex) },
                    enabled = !gameState.isGameComplete
                ) {
                    Text(
                        text = "リセット",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                    )
                }
                GameBoard(
                    gameState = gameState,
                    boardSize = boardSize,
                    ambulanceIcon = spaceShuttleIcon,
                    planetIcons = planetIcons,
                    onVehicleSelect = { puzzleViewModel.selectVehicle(it) },
                )
                Spacer(modifier = Modifier.height(32.dp))
                GridItemControl(
                    gridItem = gameState.selectedVehicleId?.let { selectedId ->
                        gameState.gridItems.find { it.id == selectedId }
                    },
                    onMove = { offset ->
                        gameState.selectedVehicleId?.let { selectedId ->
                            puzzleViewModel.moveVehicle(selectedId, offset)
                        }
                    }
                )
            }
        }
        if (showTutorial) {
            TutorialDialog(
                onDismiss = { showTutorial = false },
                onStartGame = {
                    puzzleViewModel.completeTutorial()
                    showTutorial = false
                }
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
        // グリッド線の描画
        Canvas(modifier = Modifier.size(boardSize)) {
            val cellSize = size.width / 6f
            val strokeWidth = 1.dp.toPx()

            // グリッド線の色を統一
            val gridLineColor = Color.Black.copy(alpha = 0.2f)

            // 縦線
            for (i in 0..6) {
                val x = i * cellSize
                drawLine(
                    color = gridLineColor,
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = strokeWidth,
                    blendMode = BlendMode.SrcOver,
                )
            }

            // 横線
            for (i in 0..6) {
                val y = i * cellSize
                drawLine(
                    color = gridLineColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = strokeWidth,
                    blendMode = BlendMode.SrcOver,
                )
            }
        }
    }
}

@Composable
fun GameBoard(
    gameState: GameState,
    boardSize: Dp,
    ambulanceIcon: Painter,
    planetIcons: List<Painter>,
    onVehicleSelect: (String) -> Unit,
) {
    val cellSize = boardSize / 6

    Box(
        modifier = Modifier
            .size(width = boardSize, height = boardSize + cellSize)
            .padding(top = cellSize)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(boardSize)
        ) {
            GridBackground(boardSize)
        }
        GoalCell(
            cellSize = cellSize
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(boardSize)
        ) {
            gameState.gridItems.forEach { vehicle ->
                SpaceObjectItem(
                    gridItem = vehicle,
                    isSelected = vehicle.id == gameState.selectedVehicleId,
                    onSelect = { onVehicleSelect(vehicle.id) },
                    cellSize = cellSize,
                    ambulanceIcon = ambulanceIcon,
                    planetIcons = planetIcons
                )
            }
        }
    }
}
