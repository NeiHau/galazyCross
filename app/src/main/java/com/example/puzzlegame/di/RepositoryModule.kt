package com.example.puzzlegame.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.puzzlegame.local.db.BillingDataStore
import com.example.puzzlegame.local.db.ClearedLevelsDataStore
import com.example.puzzlegame.repository.BillingRepository
import com.example.puzzlegame.repository.BillingRepositoryImpl
import com.example.puzzlegame.repository.GameRepository
import com.example.puzzlegame.repository.GameRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Provides
    @Singleton
    fun provideBillingRepository(
        @ApplicationContext context: Context,
        dataStore: BillingDataStore
    ): BillingRepository {
        return BillingRepositoryImpl(context, dataStore)
    }
}