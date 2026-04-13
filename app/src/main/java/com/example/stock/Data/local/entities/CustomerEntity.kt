package com.example.stock.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val customerName: String,
    val customerNum: String,
    val customerDebt: Double,
    val isSynced: Boolean = false
)
