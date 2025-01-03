package com.hakutogames.galaxycross.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.hakutogames.galaxycross.local.db.BillingDataStore
import com.hakutogames.galaxycross.local.db.ClearedLevelsDataStore
import com.hakutogames.galaxycross.repository.BillingRepository
import com.hakutogames.galaxycross.repository.BillingRepositoryImpl
import com.hakutogames.galaxycross.repository.GameRepository
import com.hakutogames.galaxycross.repository.GameRepositoryImpl
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