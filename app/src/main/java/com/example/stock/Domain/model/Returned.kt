package com.example.stock.Domain.model

data class Returned(
    val id: Int = 0,
    val customerId: Int,
    val returnedDate: String,
    val userId: String,
    val latitude: Double,
    val longitude: Double,
    val isSynced: Boolean = false
)
