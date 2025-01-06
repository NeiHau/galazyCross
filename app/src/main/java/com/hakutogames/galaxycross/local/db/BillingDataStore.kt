package com.hakutogames.galaxycross.local.db

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val Context.billingDataStore by preferencesDataStore(name = "billing_preferences")

    private val isPremiumKey = booleanPreferencesKey("is_premium_purchased")

    // デバイス全体での購入状態を取得
    val isPremiumPurchased: Flow<Boolean> = context.billingDataStore.data
        .catch { exception ->
            Log.e("BillingDataStore", "Error reading purchase state", exception)
            emit(emptyPreferences())
        }
        .map { preferences ->
            preferences[isPremiumKey] ?: false
        }

    // 購入状態を設定
    suspend fun setPremiumPurchased(purchased: Boolean) {
        context.billingDataStore.edit { preferences ->
            preferences[isPremiumKey] = purchased
        }
    }
}
