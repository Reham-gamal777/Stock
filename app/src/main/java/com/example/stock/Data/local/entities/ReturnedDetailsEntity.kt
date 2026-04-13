package com.example.stock.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "returned_details",
    foreignKeys = [
        ForeignKey(
            entity = ReturnedEntity::class,
            parentColumns = ["id"],
            childColumns = ["returnedId"],
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
        Index(value = ["returnedId"]),
        Index(value = ["itemId"])
    ]
)
data class ReturnedDetailsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val returnedId: Int,
    val itemId: Int,
    val amount: Int,
    val price: Double
)
