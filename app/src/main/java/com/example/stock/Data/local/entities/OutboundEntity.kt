package com.example.stock.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "outbounds",
    foreignKeys = [
        ForeignKey(
            entity = CustomerEntity::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["customerId"])]
)
data class OutboundEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
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
