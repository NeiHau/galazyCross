package com.hakutogames.galaxycross.domain

sealed class PurchaseResult {
    data object Success : PurchaseResult()
    data object Canceled : PurchaseResult()
    data class Error(val message: String?) : PurchaseResult()
}
