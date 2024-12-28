package com.example.puzzlegame.repository

import com.example.puzzlegame.local.db.ClearedLevelsDataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface GameRepository {
    val clearedLevelsFlow: Flow<Set<Int>>
    suspend fun addClearedLevel(level: Int)
}

class GameRepositoryImpl @Inject constructor(
    private val clearedLevelsDataStore: ClearedLevelsDataStore
) : GameRepository {
    override val clearedLevelsFlow get() = clearedLevelsDataStore.clearedLevels
    override suspend fun addClearedLevel(level: Int) {
        clearedLevelsDataStore.addClearedLevel(level)
    }
}