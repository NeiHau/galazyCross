package com.example.puzzlegame.ui.puzzle

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.puzzlegame.domain.GameState
import com.example.puzzlegame.domain.Vehicle
import com.example.puzzlegame.data.GameLevels.LEVELS
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RushHourViewModel : ViewModel() {
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    init {
        initializeGame(0)
    }

    fun initializeGame(level: Int) {
        _gameState.update { currentState ->
            currentState.copy(
                vehicles = LEVELS.getOrNull(level) ?: LEVELS[0],
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
            val vehicle = currentState.vehicles.find { it.id == id } ?: return@launch

            // 移動が有効でない場合は早期リターン
            if (!isValidPosition(vehicle, newPosition, currentState.vehicles)) return@launch

            // 車両の移動を含む新しい状態を作成
            val stateWithMovedVehicle = currentState.copy(
                vehicles = currentState.vehicles.map { v ->
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

            // 勝利判定を行い、最終的な状態を作成
            val finalState = checkWin(stateWithMovedVehicle)

            // StateFlowを更新
            _gameState.update { finalState }
        }
    }

    private fun checkWin(state: GameState): GameState {
        val targetVehicle = state.vehicles.find { it.isTarget }
        val isWin = targetVehicle?.position?.x == BOARD_SIZE - 2f

        // 新しい状態を作成して返す
        return state.copy(isGameComplete = isWin)
    }

    private fun isValidPosition(vehicle: Vehicle, newPosition: Offset, vehicles: List<Vehicle>): Boolean {
        val vehicleBounds = if (vehicle.isHorizontal) {
            // 水平方向の車両の占有範囲
            (newPosition.x.toInt() until (newPosition.x + vehicle.length).toInt()).map { x ->
                Pair(x, newPosition.y.toInt())
            }
        } else {
            // 垂直方向の車両の占有範囲
            (newPosition.y.toInt() until (newPosition.y + vehicle.length).toInt()).map { y ->
                Pair(newPosition.x.toInt(), y)
            }
        }

        // ボード範囲外のチェック
        if (vehicleBounds.any { (x, y) -> x !in 0 until BOARD_SIZE || y !in 0 until BOARD_SIZE }) {
            return false
        }

        // 他の車両と衝突しないことを確認
        val otherVehiclesBounds = vehicles.filter { it.id != vehicle.id }.flatMap { otherVehicle ->
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

        // 車両の新しい位置が他の車両と衝突しないか確認
        return !vehicleBounds.any { it in otherVehiclesBounds }
    }

    companion object {
        const val BOARD_SIZE = 6
    }
}