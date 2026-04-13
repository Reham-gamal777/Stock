package com.example.stock.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "outbound_details",
    foreignKeys = [
        ForeignKey(
            entity = OutboundEntity::class,
            parentColumns = ["id"],
            childColumns = ["outboundId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["outboundId"]),
        Index(value = ["itemId"])
    ]
)
data class OutboundDetailsEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val outboundId: Long,
    val itemId: Int,
    val amount: Int,
    val price: Double,
    val isSynced: Boolean = false
)
