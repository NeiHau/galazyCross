package com.example.puzzlegame.repository

import com.example.puzzlegame.local.db.ClearedLevelsDataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface GameRepository {
    val clearedLevelsFlow: Flow<Set<Int>>
    suspend fun addClearedLevel(level: Int)
    val isTutorialCompletedFlow: Flow<Boolean>
    suspend fun completeTutorial()
}

class GameRepositoryImpl @Inject constructor(
    private val clearedLevelsDataStore: ClearedLevelsDataStore
) : GameRepository {
    override val clearedLevelsFlow get() = clearedLevelsDataStore.clearedLevels
    override suspend fun addClearedLevel(level: Int) {
        clearedLevelsDataStore.addClearedLevel(level)
    }
    override val isTutorialCompletedFlow: Flow<Boolean> = clearedLevelsDataStore.isTutorialCompleted

    override suspend fun completeTutorial() {
        clearedLevelsDataStore.completeTutorial()
    }
}