package com.example.puzzlegame.domain

import com.google.firebase.Timestamp

data class PurchaseData(
    val purchaseToken: String,
    val orderId: String,
    val productId: String,
    val purchaseTime: Timestamp,
    val deviceId: String,
    val status: String,
    val acknowledgementState: Boolean
)