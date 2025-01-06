package com.hakutogames.galaxycross.local.db

import android.content.Context
import androidx.datastore.preferences.core.Preferences
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
    private val Context.billingDataStore by preferencesDataStore(name = "billing_preferences")

    // アカウントIDごとのキーを生成
    fun getPremiumKeyForAccount(accountId: String?): Preferences.Key<Boolean> {
        return booleanPreferencesKey("is_premium_purchased_$accountId")
    }

    suspend fun setPremiumPurchasedForAccount(accountId: String?, purchased: Boolean) {
        context.billingDataStore.edit { preferences ->
            preferences[getPremiumKeyForAccount(accountId)] = purchased
        }
    }

    fun isPremiumPurchasedForAccount(accountId: String?): Flow<Boolean> {
        return context.billingDataStore.data.map { preferences ->
            preferences[getPremiumKeyForAccount(accountId)] ?: false
        }
    }

    // 代わりに、アカウントIDを引数に取る形に変更(するなら下記のようにする)
    suspend fun savePurchaseInfoForAccount(
        accountId: String?,
        purchaseToken: String,
        purchaseTime: Long,
        orderId: String,
    ) {
        context.billingDataStore.edit { preferences ->
            // 好みでKeyを動的に作成してもOK
            preferences[stringPreferencesKey("purchase_token_$accountId")] = purchaseToken
            preferences[longPreferencesKey("purchase_time_$accountId")] = purchaseTime
            preferences[stringPreferencesKey("order_id_$accountId")] = orderId
            preferences[getPremiumKeyForAccount(accountId)] = true
        }
    }

    companion object {
        private val PURCHASE_TOKEN_KEY = stringPreferencesKey("purchase_token")
        private val PURCHASE_TIME_KEY = longPreferencesKey("purchase_time")
        private val ORDER_ID_KEY = stringPreferencesKey("order_id")
    }
}
