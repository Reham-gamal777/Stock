package com.example.stock.Domain.model

data class InboundDetails(
    val id: Int = 0,
    val inboundId: Int,
    val itemId: Int,
    val amount: Int,
    val isSynced: Boolean = false
)
