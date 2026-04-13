package com.example.stock.Domain.model

data class ReturnedDetails(
    val id: Int = 0,
    val returnedId: Int,
    val itemId: Int,
    val amount: Int,
    val price: Double
)
