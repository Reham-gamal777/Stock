package com.example.stock.Domain.model

data class Customer(
    val id: Int = 0,
    val userId: String,
    val customerName: String,
    val customerNum: String,
    val customerDebt: Double,
    val isSynced: Boolean = false
)
