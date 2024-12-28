package com.example.puzzlegame.di

import com.example.puzzlegame.local.db.ClearedLevelsDataStore
import com.example.puzzlegame.repository.GameRepository
import com.example.puzzlegame.repository.GameRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideGameRepository(
        clearedLevelsDataStore: ClearedLevelsDataStore
    ): GameRepository {
        return GameRepositoryImpl(clearedLevelsDataStore)
    }
}