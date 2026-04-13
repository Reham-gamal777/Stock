package com.example.stock.Domain.model

data class Stock(
    val id: Int = 0,
    val itemId: Int,
    val userId: String,
    val initAmount: Int,
    val currentAmount: Int,
    val firstDate: String,
    val isSynced: Boolean = false
)
