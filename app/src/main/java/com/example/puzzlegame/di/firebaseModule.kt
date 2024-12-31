package com.example.puzzlegame.di

import android.content.Context
import com.example.puzzlegame.repository.BillingRepository
import com.example.puzzlegame.repository.BillingRepositoryImpl
import com.example.puzzlegame.repository.FirebaseRepository
import com.example.puzzlegame.repository.FirebaseRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFunctions(): FirebaseFunctions {
        return FirebaseFunctions.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseRepository(
        firestore: FirebaseFirestore,
    ): FirebaseRepository {
        return FirebaseRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideBillingRepository(
        @ApplicationContext context: Context,
        firebaseRepository: FirebaseRepository
    ): BillingRepository {
        return BillingRepositoryImpl(context, firebaseRepository)
    }
}