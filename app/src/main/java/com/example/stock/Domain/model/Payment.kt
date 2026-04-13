package com.example.stock.Domain.model

data class Payment(
    val id: Int = 0,
    val customerId: Int,
    val amount: Double,
    val paymentType: String,
    val date: String
)
