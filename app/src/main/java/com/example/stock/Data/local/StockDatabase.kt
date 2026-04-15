package com.example.stock.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.stock.data.local.dao.*
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
    abstract val itemDao: ItemDao
    abstract val customerDao: CustomerDao
    abstract val stockDao: StockDao
    abstract val inboundDao: InboundDao
    abstract val outboundDao: OutboundDao
    abstract val returnedDao: ReturnedDao
    abstract val paymentDao: PaymentDao
}
