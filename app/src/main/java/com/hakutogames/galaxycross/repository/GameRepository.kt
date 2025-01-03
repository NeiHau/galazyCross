package com.hakutogames.galaxycross.repository

import com.hakutogames.galaxycross.local.db.ClearedLevelsDataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface GameRepository {
    fun getClearedLevelsFlow(): Flow<Set<Int>>
    fun getIsTutorialCompletedFlow(): Flow<Boolean>
    suspend fun addClearedLevel(level: Int)
    suspend fun completeTutorial()
}


class GameRepositoryImpl @Inject constructor(
    private val clearedLevelsDataStore: ClearedLevelsDataStore
) : GameRepository {

    override fun getClearedLevelsFlow(): Flow<Set<Int>> = clearedLevelsDataStore.clearedLevels

    override fun getIsTutorialCompletedFlow(): Flow<Boolean> = clearedLevelsDataStore.isTutorialCompleted

    override suspend fun addClearedLevel(level: Int) {
        clearedLevelsDataStore.addClearedLevel(level)
    }

    override suspend fun completeTutorial() {
        clearedLevelsDataStore.completeTutorial()
    }
}
