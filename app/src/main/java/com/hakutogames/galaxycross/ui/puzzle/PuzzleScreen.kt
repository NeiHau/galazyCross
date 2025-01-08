package com.hakutogames.galaxycross.ui.puzzle

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hakutogames.galaxycross.R
import com.hakutogames.galaxycross.data.GameLevels
import com.hakutogames.galaxycross.data.GameLevels.LEVELS
import com.hakutogames.galaxycross.domain.GameState
import com.hakutogames.galaxycross.domain.GridItem
import com.hakutogames.galaxycross.ui.common.dialog.AnswerDialog
import com.hakutogames.galaxycross.ui.common.dialog.GameClearDialog
import com.hakutogames.galaxycross.ui.common.dialog.TutorialDialog
import com.hakutogames.galaxycross.ui.puzzle.components.GameBoard
import com.hakutogames.galaxycross.ui.puzzle.components.GridItemControl
import com.hakutogames.galaxycross.ui.puzzle.components.rememberPlanetIcons

const val TUTORIAL_LEVEL_INDEX = -1

@Composable
fun PuzzleScreen(
    puzzleViewModel: PuzzleViewModel = hiltViewModel(),
    isPremiumUser: Boolean,
    isTutorialCompleted: Boolean,
    levelIndex: Int,
    onLevelCleared: (Int) -> Unit,
    onNavigateToLevel: (Int) -> Unit,
    onBackToLevelSelection: (Int?) -> Unit,
) {
    val gameState by puzzleViewModel.gameState.collectAsStateWithLifecycle()

    LaunchedEffect(levelIndex) {
        puzzleViewModel.initializeGame(levelIndex)
    }

    LaunchedEffect(gameState.isGameComplete) {
        if (gameState.isGameComplete) {
            if (levelIndex == GameLevels.TUTORIAL_LEVEL_INDEX && !isTutorialCompleted) {
                puzzleViewModel.completeTutorial()
            }
            onLevelCleared(levelIndex)
        }
    }

    PuzzleScreen(
        gameState = gameState,
        levelIndex = levelIndex,
        isPremiumUser = isPremiumUser,
        isTutorialCompleted = isTutorialCompleted,
        selectVehicle = puzzleViewModel::selectVehicle,
        moveVehicle = puzzleViewModel::moveVehicle,
        initializeGame = puzzleViewModel::initializeGame,
        onNavigateToLevel = onNavigateToLevel,
        onBackToLevelSelection = onBackToLevelSelection,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PuzzleScreen(
    gameState: GameState,
    levelIndex: Int,
    isPremiumUser: Boolean,
    isTutorialCompleted: Boolean,
    selectVehicle: (String) -> Unit,
    moveVehicle: (String, Offset) -> Unit,
    initializeGame: (Int) -> Unit,
    onNavigateToLevel: (Int) -> Unit,
    onBackToLevelSelection: (Int?) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    var showDialog by remember { mutableStateOf(false) }
    var showAnswerDialog by remember { mutableStateOf(false) }

    val showTutorialDialog = levelIndex == GameLevels.TUTORIAL_LEVEL_INDEX && !isTutorialCompleted
    var showTutorial by remember { mutableStateOf(showTutorialDialog) }

    // リソース
    val spaceShuttleIcon = painterResource(id = R.drawable.ic_space_shuttle)
    val planetIcons = rememberPlanetIcons()
    val boardSize = LocalConfiguration.current.screenWidthDp.dp - 52.dp

    LaunchedEffect(gameState.isGameComplete) {
        if (gameState.isGameComplete) {
            showDialog = true
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
                            style = MaterialTheme.typography.titleLarge,
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { onBackToLevelSelection(null) },
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "戻る",
                            )
                        }
                    },
                    actions = {
                        if (levelIndex == TUTORIAL_LEVEL_INDEX) {
                            Box {}
                        } else {
                            IconButton(onClick = { showAnswerDialog = true }) {
                                Icon(
                                    modifier = Modifier.size(40.dp),
                                    imageVector = ImageVector.vectorResource(R.drawable.ic_info_icon),
                                    contentDescription = "回答動画を表示",
                                )
                            }
                        }
                    },
                )
            },
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
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
                        .padding(bottom = 36.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White),
                    onClick = { initializeGame(levelIndex) },
                    enabled = !gameState.isGameComplete,
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
                    onVehicleSelect = { selectVehicle(it) },
                )
                Spacer(modifier = Modifier.height(24.dp))
                GridItemControl(
                    gridItem = gameState.selectedVehicleId?.let { selectedId ->
                        gameState.gridItems.find { it.id == selectedId }
                    },
                    onMove = { offset ->
                        gameState.selectedVehicleId?.let { selectedId ->
                            moveVehicle(selectedId, offset)
                        }
                    },
                )
            }
        }

        when {
            showTutorial -> {
                TutorialDialog(
                    onDismiss = { showTutorial = false },
                    onStartGame = {
                        showTutorial = false
                    },
                )
            }
            showDialog && gameState.isGameComplete -> {
                GameClearDialog(
                    currentLevel = levelIndex,
                    isPremiumUser = isPremiumUser,
                    hasNextLevel = levelIndex < LEVELS.size - 1,
                    onReplay = {
                        showDialog = false
                        initializeGame(levelIndex)
                    },
                    onNextLevel = {
                        showDialog = false
                        onNavigateToLevel(levelIndex + 1)
                    },
                    onShowLevelSelection = { scrollIndex ->
                        showDialog = false
                        focusManager.clearFocus()
                        onBackToLevelSelection(scrollIndex)
                    },
                )
            }
            showAnswerDialog -> {
                AnswerDialog(
                    onDismiss = { showAnswerDialog = false },
                    levelIndex = levelIndex,
                )
            }
        }
    }
}

@Composable
fun GridBackground(boardSize: Dp) {
    Box(modifier = Modifier.size(boardSize)) {
        Box(
            modifier = Modifier
                .size(boardSize)
                .background(Color(0xFFC5C5C5)),
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

@Preview(showBackground = true)
@Composable
fun PuzzleScreenPreview() {
    val mockGridItems = listOf(
        GridItem(
            id = "0",
            position = Offset(2f, 4f),
            length = 2,
            isHorizontal = false,
            isTarget = true,
            imageIndex = 1,
        ),
        GridItem(
            id = "1",
            position = Offset(0f, 1f),
            length = 2,
            isHorizontal = true,
            imageIndex = 2,
        ),
        GridItem(
            id = "2",
            position = Offset(3f, 1f),
            length = 2,
            isHorizontal = true,
            imageIndex = 3,
        ),
        GridItem(
            id = "3",
            position = Offset(0f, 2f),
            length = 2,
            isHorizontal = false,
            imageIndex = 4,
        ),
        GridItem(
            id = "4",
            position = Offset(1f, 2f),
            length = 2,
            isHorizontal = false,
            imageIndex = 5,
        ),
        GridItem(
            id = "5",
            position = Offset(2f, 2f),
            length = 3,
            isHorizontal = true,
            imageIndex = 6,
        ),
        GridItem(
            id = "6",
            position = Offset(2f, 3f),
            length = 2,
            isHorizontal = true,
            imageIndex = 7,
        ),
        GridItem(
            id = "7",
            position = Offset(4f, 3f),
            length = 2,
            isHorizontal = true,
            imageIndex = 8,
        ),
        GridItem(
            id = "8",
            position = Offset(0f, 4f),
            length = 2,
            isHorizontal = true,
            imageIndex = 9,
        ),
        GridItem(
            id = "9",
            position = Offset(0f, 5f),
            length = 2,
            isHorizontal = true,
            imageIndex = 10,
        ),
    )
    val mockGameState = GameState(
        isGameComplete = false,
        selectedVehicleId = null,
        gridItems = mockGridItems,
    )

    PuzzleScreen(
        gameState = mockGameState,
        levelIndex = 1,
        isPremiumUser = false,
        isTutorialCompleted = true,
        selectVehicle = {},
        moveVehicle = { _, _ -> },
        initializeGame = {},
        onNavigateToLevel = {},
        onBackToLevelSelection = {},
    )
}
