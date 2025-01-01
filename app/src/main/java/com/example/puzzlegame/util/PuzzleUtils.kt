package com.example.puzzlegame.util

import androidx.compose.ui.geometry.Offset
import com.example.puzzlegame.domain.GridItem

object PuzzleUtils {
    const val BOARD_SIZE = 6
    const val GOAL_X = 2f      // X-coordinate of the goal (third from the left)
    const val GOAL_Y = -1f     // Y-coordinate of the goal (one cell above the grid)

    /**
     * Validates if a grid item can be moved to the new position without overlapping
     * and staying within the board boundaries.
     *
     * @param gridItem The vehicle to move.
     * @param newPosition The desired new position.
     * @param gridItems All current grid items on the board.
     * @return True if the position is valid, false otherwise.
     */
    fun isValidPosition(
        gridItem: GridItem,
        newPosition: Offset,
        gridItems: List<GridItem>
    ): Boolean {
        // Define valid Y range based on whether the item is the target
        val validYRange = if (gridItem.isTarget) {
            (GOAL_Y.toInt() until BOARD_SIZE)
        } else {
            (0 until BOARD_SIZE)
        }

        // Calculate the bounds of the vehicle based on new position
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

        // Check if any part of the vehicle is out of bounds
        if (vehicleBounds.any { (x, y) ->
                x !in 0 until BOARD_SIZE ||
                        y !in validYRange
            }
        ) {
            return false
        }

        // Get the occupied tiles by other vehicles
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

        // Ensure no overlap with other vehicles
        if (vehicleBounds.any { it in otherVehiclesBounds }) {
            return false
        }

        // If all checks pass, the position is valid
        return true
    }
}
