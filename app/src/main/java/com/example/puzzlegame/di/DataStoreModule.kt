package com.example.puzzlegame.di

import android.content.Context
import com.example.puzzlegame.local.db.BillingDataStore
import com.example.puzzlegame.local.db.ClearedLevelsDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// DataStoreModule.kt
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideClearedLevelsDataStore(
        @ApplicationContext context: Context
    ): ClearedLevelsDataStore {
        return ClearedLevelsDataStore(context)
    }

    @Provides
    @Singleton
    fun provideBillingDataStore(
        @ApplicationContext context: Context
    ): BillingDataStore {
        return BillingDataStore(context)
    }
}
