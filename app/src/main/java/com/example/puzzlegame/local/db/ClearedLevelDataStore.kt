package com.example.puzzlegame.local.db

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
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
    private val tutorialCompletedKey = booleanPreferencesKey("tutorial_completed")

    val clearedLevels: Flow<Set<Int>> = context.dataStore.data
        .map { preferences ->
            preferences[clearedLevelsKey]?.split(",")
                ?.mapNotNull { it.toIntOrNull() }
                ?.toSet() ?: emptySet()
        }

    private val Context.tutorialDataStore by preferencesDataStore(
        name = "tutorial_preferences"
    )

    // チュートリアルのステップを管理するための型
    enum class TutorialStep {
        NOT_STARTED,
        GAME_EXPLANATION,
        HORIZONTAL_GRID,
        VERTICAL_GRID,
        GOAL_EXPLANATION,
        COMPLETED
    }

    // チュートリアルの進行状態を保存するKey
    private val TUTORIAL_STEP = intPreferencesKey("tutorial_step")
    private val IS_TUTORIAL_SEEN = booleanPreferencesKey("is_tutorial_seen")

    // チュートリアルの進行状態を取得
    val tutorialStep: Flow<TutorialStep> = context.tutorialDataStore.data
        .map { preferences ->
            TutorialStep.values()[preferences[TUTORIAL_STEP] ?: 0]
        }

    // チュートリアルの表示状態を取得
    val isTutorialSeen: Flow<Boolean> = context.tutorialDataStore.data
        .map { preferences ->
            preferences[IS_TUTORIAL_SEEN] ?: false
        }

    // チュートリアルのステップを更新
    suspend fun updateTutorialStep(step: TutorialStep) {
        context.tutorialDataStore.edit { preferences ->
            preferences[TUTORIAL_STEP] = step.ordinal
        }
    }

    // チュートリアル完了を記録
    suspend fun markTutorialAsSeen() {
        context.tutorialDataStore.edit { preferences ->
            preferences[IS_TUTORIAL_SEEN] = true
        }
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

    val isTutorialCompleted: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[tutorialCompletedKey] ?: false
        }

    suspend fun completeTutorial() {
        context.dataStore.edit { preferences ->
            preferences[tutorialCompletedKey] = true
        }
    }
}


