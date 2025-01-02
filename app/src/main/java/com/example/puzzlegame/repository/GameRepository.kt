package com.example.puzzlegame.repository

import com.example.puzzlegame.local.db.ClearedLevelsDataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface GameRepository {
    fun getClearedLevelsFlow(): Flow<Set<Int>>
    suspend fun addClearedLevel(level: Int)
    fun getIsTutorialCompletedFlow(): Flow<Boolean>
    suspend fun completeTutorial()
}


class GameRepositoryImpl @Inject constructor(
    private val clearedLevelsDataStore: ClearedLevelsDataStore
) : GameRepository {

    override fun getClearedLevelsFlow(): Flow<Set<Int>> = clearedLevelsDataStore.clearedLevels

    override suspend fun addClearedLevel(level: Int) {
        clearedLevelsDataStore.addClearedLevel(level)
    }

    override fun getIsTutorialCompletedFlow(): Flow<Boolean> = clearedLevelsDataStore.isTutorialCompleted

    override suspend fun completeTutorial() {
        clearedLevelsDataStore.completeTutorial()
    }
}
