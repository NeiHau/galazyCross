package com.example.puzzlegame.ui.puzzle

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.galaxycross.R
import com.example.puzzlegame.data.GameLevels.LEVELS
import com.example.puzzlegame.domain.GameState
import com.example.puzzlegame.ui.puzzle.components.GameClearDialog
import com.example.puzzlegame.ui.puzzle.components.GridItemControl
import com.example.puzzlegame.ui.puzzle.components.TutorialAnimationView
import com.example.puzzlegame.ui.puzzle.components.SpaceObjectItem
import com.example.puzzlegame.ui.puzzle.components.rememberPlanetIcons
import kotlin.math.roundToInt

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

    // チュートリアル管理
    var showInitialDialog by remember { mutableStateOf(true) }

    // リソースの準備
    val ambulanceIcon = painterResource(id = R.drawable.ic_space_shuttle)
    val planetIcons = rememberPlanetIcons()
    val boardSize = LocalConfiguration.current.screenWidthDp.dp - 56.dp

    LaunchedEffect(levelIndex) {
        puzzleViewModel.initializeGame(levelIndex)
    }

    // ゲームクリア時の処理
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
            // ゲームクリアダイアログの表示
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

            // 背景の設定
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.universe_space01),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }

            // メインコンテンツ
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // リセットボタン
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
                    ambulanceIcon = ambulanceIcon,
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

        // 初期ルール説明ダイアログの表示
        if (!isTutorialCompleted) {
            TutorialDialog(
                onDismiss = { showInitialDialog = false },
                onStartGame = {
                    puzzleViewModel.completeTutorial()
                    showInitialDialog = false
                }
            )
        }

        // チュートリアルステップの表示
//        if (shouldCoach && tutorialIndex in 1..4) {
//            TutorialOverlay(
//                tutorialIndex = tutorialIndex,
//                onStepComplete = {
//                    if (tutorialIndex < 4) {
//                        tutorialIndex += 1
//                    } else {
//                        shouldCoach = false
//                    }
//                }
//            )
//        }
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
            // アルファ値を0.3fに設定して、より繊細な線を描画
            val gridLineColor = Color.Black.copy(alpha = 0.2f)

            // 縦線を描画
            for (i in 0..6) {
                val x = i * cellSize
                drawLine(
                    color = gridLineColor,
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = strokeWidth,
                    // アンチエイリアスを有効にして、線をよりスムーズに
                    blendMode = BlendMode.SrcOver,
                )
            }

            // 横線を描画
            for (i in 0..6) {
                val y = i * cellSize
                drawLine(
                    color = gridLineColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = strokeWidth,
                    // アンチエイリアスを有効にして、線をよりスムーズに
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
    // ゴールマス分の余白を追加
    val cellSize = boardSize / 6

    Box(
        modifier = Modifier
            .size(width = boardSize, height = boardSize + cellSize)  // 高さを1マス分増やす
            .padding(top = cellSize)  // 上部にスペースを確保
    ) {
        // グリッド背景を下にずらして配置
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)  // 下部に寄せる
                .size(boardSize)
        ) {
            GridBackground(boardSize)
        }
        // ゴールマスを追加
        GoalCell(
            cellSize = cellSize
        )
        // グリッドアイテムも下にずらして配置
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

@Composable
private fun GoalCell(
    cellSize: Dp
) {
    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    x = (2 * cellSize.toPx()).roundToInt(),  // 左から3つ目
                    y = (-cellSize.toPx()).roundToInt()      // 通常のグリッドより1マス上
                )
            }
            .size(cellSize)
            .background(
                color = Color.Red,
                shape = RoundedCornerShape(4.dp)
            )
            .border(
                width = 1.dp,
                color = Color.Green,
                shape = RoundedCornerShape(4.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "ゴール",
            color = Color.Yellow,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun TutorialDialog(
    onDismiss: () -> Unit,
    onStartGame: () -> Unit
) {
    // Use DialogProperties to disable the platform default width
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            // Allows the dialog to take custom width and height
            usePlatformDefaultWidth = false
        )
    ) {
        // Get the screen dimensions to set dialog size relative to the screen
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        val screenHeight = configuration.screenHeightDp.dp

        Surface(
            modifier = Modifier
                .width(screenWidth * 0.9f)
                .height(screenHeight * 0.8f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                // Header Section
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ゲーム説明",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                    TutorialAnimationView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(screenHeight * 0.45f)
                            .width(screenWidth * 0.8f)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        buildAnnotatedString {
                            append("アイテムをタップし、矢印で進行方向の前後に移動できるよ！。星を避けて宇宙船をゴールへ。リセットは")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("「リセット」")
                            }
                            append("をタップ！")
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        textAlign = TextAlign.Center,
                    )

                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onStartGame,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(
                        text = "ゲームを開始する",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp)
                    )
                }
            }
        }
    }
}
