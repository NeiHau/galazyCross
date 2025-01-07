package com.hakutogames.galaxycross.di

import com.hakutogames.galaxycross.application.usecase.GoogleBillingUseCase
import com.hakutogames.galaxycross.application.usecase.GoogleBillingUseCaseImpl
import com.hakutogames.galaxycross.repository.BillingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGoogleBillingUseCase(
        billingRepository: BillingRepository,
    ): GoogleBillingUseCase = GoogleBillingUseCaseImpl(billingRepository)
}
