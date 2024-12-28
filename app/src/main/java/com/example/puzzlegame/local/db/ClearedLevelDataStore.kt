package com.example.puzzlegame.local.db

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Kotlin の例
@Singleton
class ClearedLevelsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val Context.dataStore by preferencesDataStore("cleared_levels")

    // 既存のコードをクラス内にそのまま入れる
    private val clearedLevelsKey = stringPreferencesKey("cleared_levels")

    val clearedLevels: Flow<Set<Int>> = context.dataStore.data
        .map { preferences ->
            preferences[clearedLevelsKey]?.split(",")
                ?.mapNotNull { it.toIntOrNull() }
                ?.toSet() ?: emptySet()
        }

    suspend fun addClearedLevel(level: Int) {
        context.dataStore.edit { preferences ->
            val currentLevels = preferences[clearedLevelsKey]
                ?.split(",")
                ?.mapNotNull { it.toIntOrNull() }
                ?.toSet()
                ?: emptySet()
            val updatedLevels = currentLevels + level
            preferences[clearedLevelsKey] = updatedLevels.joinToString(",")
        }
    }
}


