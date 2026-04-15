package com.example.stock.data.local.dao

import androidx.room.*
import com.example.stock.data.local.entities.ReturnedDetailsEntity
import com.example.stock.data.local.entities.ReturnedEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReturnedDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReturned(returned: ReturnedEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReturnedDetails(details: List<ReturnedDetailsEntity>)

    @Query("SELECT * FROM returned")
    fun getAllReturned(): Flow<List<ReturnedEntity>>

    @Query("SELECT * FROM returned_details WHERE returnedId = :returnedId")
    suspend fun getDetailsForReturned(returnedId: Int): List<ReturnedDetailsEntity>
    
    @Query("SELECT * FROM returned_details WHERE itemId = :itemId")
    suspend fun getReturnedDetailsByItem(itemId: Int): List<ReturnedDetailsEntity>
}
