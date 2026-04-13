package com.example.stock.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "inbounds",
    indices = [Index(value = ["invoiceNumber"])]
)
data class InboundEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val invoiceNumber: String,
    val supplierName: String,
    val date: String,
    val totalAmount: Double = 0.0,
    val status: String = "معلقة", // "مكتملة" أو "معلقة"
    val description: String = "",
    val isSynced: Boolean = false
)
