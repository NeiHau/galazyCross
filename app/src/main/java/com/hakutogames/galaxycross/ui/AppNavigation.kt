package com.hakutogames.galaxycross.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.hakutogames.galaxycross.data.GameLevels
import com.hakutogames.galaxycross.ui.levelselection.LevelSelectionScreen
import com.hakutogames.galaxycross.ui.levelselection.LevelSelectionViewModel
import com.hakutogames.galaxycross.ui.puzzle.PuzzleScreen
import com.hakutogames.galaxycross.ui.puzzle.PuzzleViewModel
import com.hakutogames.galaxycross.ui.setting.SettingsScreen
import com.hakutogames.galaxycross.ui.terms.TermsScreen

sealed class Screen(val route: String) {
    data object LevelSelection : Screen("levelSelection")
    data object Game : Screen("game/{levelIndex}") {
        fun createRoute(levelIndex: Int) = "game/$levelIndex"
    }
    data object Setting : Screen("setting")
    data object Terms : Screen("terms")
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    onShowSnackbar: (String) -> Unit,
) {
    val levelSelectionViewModel: LevelSelectionViewModel = hiltViewModel()
    val puzzleViewModel: PuzzleViewModel = hiltViewModel()

    val clearedLevels by puzzleViewModel.clearedLevels.collectAsStateWithLifecycle()
    val isTutorialCompleted by puzzleViewModel.isTutorialCompleted.collectAsStateWithLifecycle()
    val isPremiumUser by levelSelectionViewModel.isPremiumPurchased.collectAsStateWithLifecycle()

    var isReturningToLevelSelection by remember { mutableStateOf(false) }

    NavHost(
        navController = navController,
        startDestination = Screen.LevelSelection.route,
    ) {
        composable(Screen.LevelSelection.route) {
            LevelSelectionScreen(
                clearedLevels = clearedLevels,
                isTutorialCompleted = isTutorialCompleted,
                onShowSnackbar = onShowSnackbar,
                onTutorialSelect = {
                    puzzleViewModel.initializeGame(GameLevels.TUTORIAL_LEVEL_INDEX)
                    navController.navigate(Screen.Game.createRoute(GameLevels.TUTORIAL_LEVEL_INDEX))
                },
                onLevelSelect = { levelIndex ->
                    puzzleViewModel.initializeGame(levelIndex)
                    navController.navigate(Screen.Game.createRoute(levelIndex)) {
                        popUpTo(Screen.LevelSelection.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onSettingIconTapped = {
                    navController.navigate(Screen.Setting.route)
                },
                isReturningToLevelSelection = isReturningToLevelSelection,
            )
        }

        composable(
            route = Screen.Game.route,
            arguments = listOf(
                navArgument("levelIndex") { type = NavType.IntType },
            ),
        ) { backStackEntry ->
            val levelIndex = backStackEntry.arguments?.getInt("levelIndex") ?: 0

            PuzzleScreen(
                isPremiumUser = isPremiumUser,
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
                    if (!isPremiumUser && levelIndex == 14) {
                        levelSelectionViewModel.setScrollToLevelIndex(15)
                        isReturningToLevelSelection = true
                    }
                    navController.navigate(Screen.LevelSelection.route) {
                        popUpTo(0)
                    }
                },
            )
        }

        composable(route = Screen.Setting.route) {
            SettingsScreen(
                onAppBarBackButtonTapped = {
                    navController.popBackStack()
                },
                onTermsTapped = {
                    navController.navigate(Screen.Terms.route)
                },
            )
        }

        composable(route = Screen.Terms.route) {
            TermsScreen(
                onBackButtonTapped = {
                    navController.popBackStack()
                },
            )
        }
    }
}
