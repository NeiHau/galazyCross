package com.example.puzzlegame.ui.puzzle

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.puzzlegame.data.GameLevels
import com.example.puzzlegame.domain.GameState
import com.example.puzzlegame.domain.GridItem
import com.example.puzzlegame.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PuzzleViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    // 現在のレベルを追跡するための変数を追加
    private var currentLevel: Int = 0

    val clearedLevels: StateFlow<Set<Int>> = gameRepository.clearedLevelsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    fun addClearedLevel(level: Int) {
        viewModelScope.launch {
            gameRepository.addClearedLevel(level)
        }
    }

    fun initializeGame(level: Int) {
        currentLevel = level
        val vehicles = GameLevels.getRandomizedLevel(level)

        _gameState.update { currentState ->
            currentState.copy(
                gridItems = vehicles,
                selectedVehicleId = null,
                isGameComplete = false
            )
        }
    }

    fun selectVehicle(id: String?) {
        _gameState.update { currentState ->
            currentState.copy(selectedVehicleId = id)
        }
    }

    fun moveVehicle(id: String, newPosition: Offset) {
        viewModelScope.launch {
            val currentState = _gameState.value
            val vehicle = currentState.gridItems.find { it.id == id } ?: return@launch

            if (!isValidPosition(vehicle, newPosition, currentState.gridItems)) return@launch

            val stateWithMovedVehicle = currentState.copy(
                gridItems = currentState.gridItems.map { v ->
                    if (v.id == id) {
                        v.copy(
                            position = newPosition.copy(
                                x = newPosition.x.coerceIn(
                                    0f,
                                    BOARD_SIZE - (if (v.isHorizontal) v.length else 1).toFloat()
                                ),
                                y = newPosition.y.coerceIn(
                                    0f,
                                    BOARD_SIZE - (if (!v.isHorizontal) v.length else 1).toFloat()
                                )
                            )
                        )
                    } else v
                }
            )

            val finalState = checkWin(stateWithMovedVehicle)
            _gameState.update { finalState }

            if (finalState.isGameComplete) {
                markLevelCleared(currentLevel)
            }
        }
    }

    private fun checkWin(state: GameState): GameState {
        val targetVehicle = state.gridItems.find { it.isTarget }
        // 勝利条件を垂直方向の最上部到達に変更
        val isWin = targetVehicle?.position?.y == 0f

        return state.copy(isGameComplete = isWin)
    }

    private fun markLevelCleared(level: Int) {
        viewModelScope.launch {
            gameRepository.addClearedLevel(level)
        }
    }

    private fun isValidPosition(gridItem: GridItem, newPosition: Offset, gridItems: List<GridItem>): Boolean {
        val vehicleBounds = if (gridItem.isHorizontal) {
            (newPosition.x.toInt() until (newPosition.x + gridItem.length).toInt()).map { x ->
                Pair(x, newPosition.y.toInt())
            }
        } else {
            (newPosition.y.toInt() until (newPosition.y + gridItem.length).toInt()).map { y ->
                Pair(newPosition.x.toInt(), y)
            }
        }

        if (vehicleBounds.any { (x, y) -> x !in 0 until BOARD_SIZE || y !in 0 until BOARD_SIZE }) {
            return false
        }

        val otherVehiclesBounds = gridItems.filter { it.id != gridItem.id }.flatMap { otherVehicle ->
            if (otherVehicle.isHorizontal) {
                (otherVehicle.position.x.toInt() until (otherVehicle.position.x + otherVehicle.length).toInt()).map { x ->
                    Pair(x, otherVehicle.position.y.toInt())
                }
            } else {
                (otherVehicle.position.y.toInt() until (otherVehicle.position.y + otherVehicle.length).toInt()).map { y ->
                    Pair(otherVehicle.position.x.toInt(), y)
                }
            }
        }.toSet()

        return !vehicleBounds.any { it in otherVehiclesBounds }
    }

    companion object {
        const val BOARD_SIZE = 6
    }
}
