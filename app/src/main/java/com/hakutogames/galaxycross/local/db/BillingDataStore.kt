package com.hakutogames.galaxycross.local.db

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val Context.billingDataStore by preferencesDataStore(
        name = "billing_preferences"
    )

    // プレミアム課金状態を取得するFlow
    val isPremiumPurchased: Flow<Boolean> = context.billingDataStore.data
        .map { preferences ->
            preferences[PREMIUM_PURCHASED_KEY] ?: false
        }

    // プレミアム課金状態を保存
    suspend fun setPremiumPurchased(purchased: Boolean) {
        context.billingDataStore.edit { preferences ->
            preferences[PREMIUM_PURCHASED_KEY] = purchased
        }
    }

    // 購入情報を保存
    suspend fun savePurchaseInfo(
        purchaseToken: String,
        purchaseTime: Long,
        orderId: String
    ) {
        context.billingDataStore.edit { preferences ->
            preferences[PURCHASE_TOKEN_KEY] = purchaseToken
            preferences[PURCHASE_TIME_KEY] = purchaseTime
            preferences[ORDER_ID_KEY] = orderId
            preferences[PREMIUM_PURCHASED_KEY] = true
        }
    }

    companion object {
        private val PREMIUM_PURCHASED_KEY = booleanPreferencesKey("is_premium_purchased")
        private val PURCHASE_TOKEN_KEY = stringPreferencesKey("purchase_token")
        private val PURCHASE_TIME_KEY = longPreferencesKey("purchase_time")
        private val ORDER_ID_KEY = stringPreferencesKey("order_id")
    }
}