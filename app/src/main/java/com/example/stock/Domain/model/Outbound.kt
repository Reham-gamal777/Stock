package com.example.stock.Domain.model

data class Outbound(
    val id: Int = 0,
    val userId: String,
    val customerId: Int,
    val invoiceNumber: String,
    val image: String,
    val outboundDate: String,
    val latitude: Double,
    val longitude: Double,
    val moneyReceived: Int,
    val isSynced: Boolean = false
)
