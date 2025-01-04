package com.hakutogames.galaxycross.di

import android.content.Context
import com.hakutogames.galaxycross.local.db.BillingDataStore
import com.hakutogames.galaxycross.local.db.ClearedLevelsDataStore
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
        @ApplicationContext context: Context,
    ): ClearedLevelsDataStore {
        return ClearedLevelsDataStore(context)
    }

    @Provides
    @Singleton
    fun provideBillingDataStore(
        @ApplicationContext context: Context,
    ): BillingDataStore {
        return BillingDataStore(context)
    }
}
