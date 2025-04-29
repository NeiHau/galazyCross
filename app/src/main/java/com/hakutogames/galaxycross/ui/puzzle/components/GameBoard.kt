package com.hakutogames.galaxycross.ui.puzzle.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import com.hakutogames.galaxycross.domain.GameState
import com.hakutogames.galaxycross.ui.puzzle.GridBackground

@Composable
fun GameBoard(
    gameState: GameState,
    boardSize: Dp,
    ambulanceIcon: Painter,
    planetIcons: List<Painter>,
    onVehicleSelect: (String) -> Unit,
    onVehicleMove: (String, Offset) -> Unit,
) {
    val cellSize = boardSize / 6

    Box(
        modifier = Modifier
            .size(width = boardSize, height = boardSize + cellSize)
            .padding(top = cellSize),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(boardSize),
        ) {
            GridBackground(boardSize)
        }
        GoalCell(
            cellSize = cellSize,
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(boardSize),
        ) {
            gameState.gridItems.forEach { vehicle ->
                SpaceObjectItem(
                    gridItem = vehicle,
                    isSelected = vehicle.id == gameState.selectedVehicleId,
                    onSelect = { onVehicleSelect(vehicle.id) },
                    onMove = { newPosition -> onVehicleMove(vehicle.id, newPosition) },
                    cellSize = cellSize,
                    ambulanceIcon = ambulanceIcon,
                    planetIcons = planetIcons,
                )
            }
        }
    }
}
