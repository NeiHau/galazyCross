package com.hakutogames.galaxycross.ui.puzzle

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hakutogames.galaxycross.data.GameLevels
import com.hakutogames.galaxycross.domain.GameState
import com.hakutogames.galaxycross.domain.GridItem
import com.hakutogames.galaxycross.repository.BillingRepository
import com.hakutogames.galaxycross.repository.GameRepository
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
    private val gameRepository: GameRepository,
    billingRepository: BillingRepository,
) : ViewModel() {
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private var currentLevel: Int = 0

    val isPremiumPurchased: StateFlow<Boolean> = billingRepository
        .getPurchaseState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val clearedLevels: StateFlow<Set<Int>> = gameRepository.getClearedLevelsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val isTutorialCompleted: StateFlow<Boolean> = gameRepository.getIsTutorialCompletedFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false,
        )

    fun completeTutorial() {
        viewModelScope.launch {
            gameRepository.completeTutorial()
        }
    }

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
                isGameComplete = false,
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

            // ターゲット車両の場合は、ゴールまでの移動を許可
            val minY = if (vehicle.isTarget) GOAL_Y else 0f

            if (!isValidPosition(vehicle, newPosition, currentState.gridItems)) return@launch

            val stateWithMovedVehicle = currentState.copy(
                gridItems = currentState.gridItems.map { v ->
                    if (v.id == id) {
                        v.copy(
                            position = newPosition.copy(
                                x = newPosition.x.coerceIn(
                                    0f,
                                    BOARD_SIZE - (if (v.isHorizontal) v.length else 1).toFloat(),
                                ),
                                y = newPosition.y.coerceIn(
                                    minY, // ターゲットはゴールまで移動可能
                                    BOARD_SIZE - (if (!v.isHorizontal) v.length else 1).toFloat(),
                                ),
                            ),
                        )
                    } else {
                        v
                    }
                },
            )

            val finalState = checkWin(stateWithMovedVehicle)
            _gameState.update { finalState }

            if (finalState.isGameComplete) {
                markLevelCleared(currentLevel)
            }
        }
    }

    private fun checkWin(state: GameState): GameState {
        val targetVehicle = state.gridItems.find { it.isTarget } ?: return state

        // ターゲット車両が占有しているマス一覧を取得
        val occupiedTiles = if (targetVehicle.isHorizontal) {
            val xStart = targetVehicle.position.x.toInt()
            val xEnd = (targetVehicle.position.x + targetVehicle.length).toInt() - 1
            val y = targetVehicle.position.y.toInt()
            (xStart..xEnd).map { x -> x to y }
        } else {
            val yStart = targetVehicle.position.y.toInt()
            val yEnd = (targetVehicle.position.y + targetVehicle.length).toInt() - 1
            val x = targetVehicle.position.x.toInt()
            (yStart..yEnd).map { y -> x to y }
        }

        // ゴール枡は (GOAL_X=2, GOAL_Y=-1) なので、intにすると (2, -1)
        // ターゲット車両がゴール枡を“含んでいれば”クリア
        val isWin = occupiedTiles.any { (x, y) ->
            x == GOAL_X.toInt() && y == GOAL_Y.toInt()
        }

        return state.copy(isGameComplete = isWin)
    }

    private fun markLevelCleared(level: Int) {
        viewModelScope.launch {
            gameRepository.addClearedLevel(level)
        }
    }

    private fun isValidPosition(
        gridItem: GridItem,
        newPosition: Offset,
        gridItems: List<GridItem>,
    ): Boolean {
        // まずターゲット車両かどうかで、有効な Y 座標範囲を変える
        val validYRange = if (gridItem.isTarget) {
            // ゴールの -1 から ボードサイズ - 1 まで許容
            (GOAL_Y.toInt() until BOARD_SIZE)
        } else {
            // 通常車両は 0 から ボードサイズ - 1 まで
            (0 until BOARD_SIZE)
        }

        // 車両が占有しようとするマス(タイル)を割り出す
        val vehicleBounds = if (gridItem.isHorizontal) {
            val startX = newPosition.x.toInt()
            val endX = (newPosition.x + gridItem.length).toInt() - 1
            val y = newPosition.y.toInt()
            (startX..endX).map { x -> Pair(x, y) }
        } else {
            val startY = newPosition.y.toInt()
            val endY = (newPosition.y + gridItem.length).toInt() - 1
            val x = newPosition.x.toInt()
            (startY..endY).map { y -> Pair(x, y) }
        }

        // ターゲットの場合は y = -1 を含めるようにし、
        // 通常車両は 0 <= y < BOARD_SIZE のみ許す。
        // x については 0 <= x < BOARD_SIZE に収まる必要がある。
        if (vehicleBounds.any { (x, y) ->
                x !in 0 until BOARD_SIZE ||
                    y !in validYRange
            }
        ) {
            // 範囲外なら無効
            return false
        }

        // 他車両との衝突チェック
        val otherVehiclesBounds = gridItems
            .filter { it.id != gridItem.id }
            .flatMap { other ->
                if (other.isHorizontal) {
                    val startX = other.position.x.toInt()
                    val endX = (other.position.x + other.length).toInt() - 1
                    val y = other.position.y.toInt()
                    (startX..endX).map { x -> Pair(x, y) }
                } else {
                    val startY = other.position.y.toInt()
                    val endY = (other.position.y + other.length).toInt() - 1
                    val x = other.position.x.toInt()
                    (startY..endY).map { y -> Pair(x, y) }
                }
            }
            .toSet()

        // 一つでも他車両と被っていたらダメ
        if (vehicleBounds.any { it in otherVehiclesBounds }) {
            return false
        }

        // ここまで通れば移動OK
        return true
    }

    companion object {
        const val BOARD_SIZE = 6
        const val GOAL_X = 2f // ゴールのX座標（左から3つ目）
        const val GOAL_Y = -1f // ゴールのY座標（グリッドの1マス上）
    }
}
