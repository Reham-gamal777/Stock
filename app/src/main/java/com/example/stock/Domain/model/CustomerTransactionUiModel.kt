package com.example.stock.Domain.model

data class CustomerTransactionUiModel(
    val date: String,
    val type: String,
    val amount: Double,
    val representative: String = "-",
    val isPayment: Boolean
)
