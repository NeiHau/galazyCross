package com.example.rushgame.ui.rushhour

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RushHourViewModel : ViewModel() {
    // 車両を表すデータクラス - イミュータブルなデータモデル
    data class Vehicle(
        val id: String,
        val position: Offset,
        val length: Int,
        val isHorizontal: Boolean,
        val isTarget: Boolean = false
    )

    // ゲーム状態を表すデータクラス - 状態管理を整理
    data class GameState(
        val vehicles: List<Vehicle> = emptyList(),
        val selectedVehicleId: String? = null,
        val isGameComplete: Boolean = false
    )

    companion object {
        const val BOARD_SIZE = 6

        // 各レベルの初期配置
        private val LEVELS = listOf(
            // レベル 1
            listOf(
                Vehicle("target", Offset(1f, 2f), 2, true, true),
                Vehicle("truck1", Offset(0f, 0f), 3, false),
                Vehicle("car1", Offset(3f, 0f), 2, true)
            ),
            // レベル 2
            listOf(
                Vehicle("target", Offset(2f, 2f), 2, true, true),
                Vehicle("truck1", Offset(0f, 0f), 3, false),
                Vehicle("car1", Offset(4f, 0f), 2, true),
                Vehicle("car2", Offset(3f, 3f), 2, false)
            ),
            // レベル 3
            listOf(
                Vehicle("target", Offset(1f, 2f), 2, true, true),
                Vehicle("truck1", Offset(0f, 0f), 3, false),
                Vehicle("car1", Offset(3f, 0f), 2, true),
                Vehicle("car2", Offset(1f, 1f), 2, true),
                Vehicle("car3", Offset(4f, 4f), 2, false)
            ),
            // レベル 4
            listOf(
                Vehicle("target", Offset(0f, 2f), 2, true, true),
                Vehicle("truck1", Offset(0f, 0f), 3, false),
                Vehicle("car1", Offset(3f, 0f), 2, true),
                Vehicle("car2", Offset(3f, 3f), 2, false),
                Vehicle("truck2", Offset(2f, 4f), 3, true)
            ),
            // レベル 5
            listOf(
                Vehicle("target", Offset(1f, 2f), 2, true, true),
                Vehicle("truck1", Offset(0f, 3f), 3, false),
                Vehicle("car1", Offset(4f, 0f), 2, true),
                Vehicle("car2", Offset(1f, 1f), 2, true),
                Vehicle("car3", Offset(5f, 3f), 2, false),
                Vehicle("car4", Offset(2f, 3f), 2, false)
            )
        )
    }

    // 単一のStateFlowで状態を管理
    private val _gameState = MutableStateFlow(GameState(vehicles = LEVELS[4]))
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    init {
        initializeGame(4)
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
            // 現在の状態を取得
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
}