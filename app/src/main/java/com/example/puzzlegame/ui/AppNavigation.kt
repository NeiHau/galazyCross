package com.example.puzzlegame.ui

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.puzzlegame.data.GameLevels
import com.example.puzzlegame.ui.home.LevelSelectionScreen
import com.example.puzzlegame.ui.home.LevelSelectionViewModel
import com.example.puzzlegame.ui.puzzle.PuzzleScreen
import com.example.puzzlegame.ui.puzzle.PuzzleViewModel

sealed class Screen(val route: String) {
    data object LevelSelection : Screen("levelSelection")
    data object Game : Screen("game/{levelIndex}") {
        fun createRoute(levelIndex: Int) = "game/$levelIndex"
    }
}

@Composable
fun AppNavigation(
    puzzleViewModel: PuzzleViewModel = hiltViewModel(),
    levelSelectionViewModel: LevelSelectionViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val clearedLevels by puzzleViewModel.clearedLevels.collectAsState(initial = emptySet())
    val isTutorialCompleted by puzzleViewModel.isTutorialCompleted.collectAsState(initial = false)
    val isPremiumPurchased by levelSelectionViewModel.isPremiumPurchased.collectAsState()
    // val purchaseResult by levelSelectionViewModel.purchaseResult.collectAsState()

    // 課金結果の処理
//    LaunchedEffect(purchaseResult) {
//        when (purchaseResult) {
//            is BillingRepositoryImpl.PurchaseResult.Success -> {
//                // 課金成功時のメッセージ表示
//                // Toast表示のようなUIフィードバックはViewModelではなくUIレイヤーで行う
//                Toast.makeText(
//                    context,
//                    "プレミアムコンテンツが解除されました！",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//            is BillingRepositoryImpl.PurchaseResult.Error -> {
//                Toast.makeText(
//                    context,
//                    "購入処理に失敗しました",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//            is BillingRepositoryImpl.PurchaseResult.Canceled -> {
//                Toast.makeText(
//                    context,
//                    "購入がキャンセルされました",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//            else -> { /* 他の状態は無視 */ }
//        }
//    }

    NavHost(
        navController = navController,
        startDestination = Screen.LevelSelection.route
    ) {
        composable(Screen.LevelSelection.route) {
            LevelSelectionScreen(
                clearedLevels = clearedLevels,
                isTutorialCompleted = isTutorialCompleted,
                onTutorialSelect = {
                    puzzleViewModel.initializeGame(GameLevels.TUTORIAL_LEVEL_INDEX)
                    navController.navigate(Screen.Game.createRoute(GameLevels.TUTORIAL_LEVEL_INDEX))
                },
                onLevelSelect = { levelIndex ->
                    // レベルが16以降で、プレミアム未購入の場合は購入フローを開始
                    if (levelIndex >= 15 && !isPremiumPurchased) {
//                        levelSelectionViewModel.startPremiumPurchase()
                    } else {
                        puzzleViewModel.initializeGame(levelIndex)
                        navController.navigate(Screen.Game.createRoute(levelIndex)) {
                            popUpTo(Screen.LevelSelection.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
            )
        }

        composable(
            route = Screen.Game.route,
            arguments = listOf(
                navArgument("levelIndex") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val levelIndex = backStackEntry.arguments?.getInt("levelIndex") ?: 0
            PuzzleScreen(
                isTutorialCompleted = isTutorialCompleted,
                levelIndex = levelIndex,
                onLevelCleared = { clearedLevel ->
                    if (clearedLevel == GameLevels.TUTORIAL_LEVEL_INDEX) {
                        puzzleViewModel.completeTutorial()
                    } else {
                        puzzleViewModel.addClearedLevel(clearedLevel)
                    }
                },
                onNavigateToLevel = { newLevelIndex ->
                    navController.navigate(Screen.Game.createRoute(newLevelIndex)) {
                        popUpTo(Screen.Game.route) {
                            inclusive = true
                        }
                    }
                },
                onBackToLevelSelection = {
                    navController.navigate(Screen.LevelSelection.route) {
                        popUpTo(0)
                    }
                }
            )
        }
    }
}