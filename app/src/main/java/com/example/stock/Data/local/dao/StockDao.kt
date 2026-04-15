package com.example.stock.data.local.dao

import androidx.room.*
import com.example.stock.data.local.entities.StockEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StockDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStock(stock: StockEntity)

    @Query("SELECT * FROM stock")
    fun getAllStock(): Flow<List<StockEntity>>
}
