package com.example.stock.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.stock.data.local.dao.StockDao
import com.example.stock.data.local.entities.*

@Database(
    entities = [
        ItemEntity::class,
        CustomerEntity::class,
        StockEntity::class,
        InboundEntity::class,
        InboundDetailsEntity::class,
        OutboundEntity::class,
        OutboundDetailsEntity::class,
        PaymentEntity::class,
        ReturnedEntity::class,
        ReturnedDetailsEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class StockDatabase : RoomDatabase() {
    abstract val stockDao: StockDao
}
