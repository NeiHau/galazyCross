package com.example.puzzlegame.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.puzzlegame.data.GameLevels.LEVELS
import com.example.puzzlegame.ui.home.LevelSelectionScreen
import com.example.puzzlegame.ui.puzzle.PuzzleScreen

sealed class Screen(val route: String) {
    data object LevelSelection : Screen("levelSelection")
    data object Game : Screen("game/{levelIndex}") {
        fun createRoute(levelIndex: Int) = "game/$levelIndex"
    }
}

@Composable
fun RushHourNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.LevelSelection.route
    ) {
        composable(Screen.LevelSelection.route) {
            LevelSelectionScreen(
                levels = LEVELS,
                onLevelSelect = { levelIndex ->
                    navController.navigate(Screen.Game.createRoute(levelIndex)) {
                        popUpTo(Screen.LevelSelection.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
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
                levelIndex = levelIndex,
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