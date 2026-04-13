package com.example.stock.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "inbound_details",
    foreignKeys = [
        ForeignKey(
            entity = InboundEntity::class,
            parentColumns = ["id"],
            childColumns = ["inboundId"],
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
        Index(value = ["inboundId"]),
        Index(value = ["itemId"])
    ]
)
data class InboundDetailsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val inboundId: Int,
    val itemId: Int,
    val amount: Int
)
