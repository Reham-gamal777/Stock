package com.example.stock.data.local.dao

import androidx.room.*
import com.example.stock.data.local.entities.InboundDetailsEntity
import com.example.stock.data.local.entities.InboundEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InboundDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInbound(inbound: InboundEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInboundDetails(details: List<InboundDetailsEntity>)

    @Query("SELECT * FROM inbounds")
    fun getAllInbounds(): Flow<List<InboundEntity>>

    @Query("SELECT * FROM inbound_details WHERE inboundId = :inboundId")
    suspend fun getDetailsForInbound(inboundId: Int): List<InboundDetailsEntity>
    
    @Query("SELECT * FROM inbound_details WHERE itemId = :itemId")
    suspend fun getInboundDetailsByItem(itemId: Int): List<InboundDetailsEntity>
    
    @Query("SELECT * FROM inbounds WHERE id = :inboundId")
    suspend fun getInboundById(inboundId: Int): InboundEntity?
}
