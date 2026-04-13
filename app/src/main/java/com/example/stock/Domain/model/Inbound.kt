package com.example.stock.Domain.model

data class Inbound(
    val id: Int = 0,
    val invoiceNumber: String,
    val supplierName: String,
    val date: String,
    val totalAmount: Double = 0.0,
    val status: String = "معلقة",
    val description: String = "",
    val isSynced: Boolean = false
)
