package com.example.stock.data.local.dao

import androidx.room.*
import com.example.stock.data.local.entities.OutboundDetailsEntity
import com.example.stock.data.local.entities.OutboundEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OutboundDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutbound(outbound: OutboundEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutboundDetails(details: List<OutboundDetailsEntity>)

    @Transaction
    @Query("SELECT * FROM outbounds")
    fun getAllOutbounds(): Flow<List<OutboundEntity>>

    @Query("SELECT * FROM outbounds WHERE customerId = :customerId")
    suspend fun getOutboundsForCustomer(customerId: Int): List<OutboundEntity>

    @Query("SELECT * FROM outbound_details WHERE outboundId = :outboundId")
    suspend fun getDetailsForOutbound(outboundId: Long): List<OutboundDetailsEntity>
    
    @Query("SELECT * FROM outbound_details WHERE itemId = :itemId")
    suspend fun getOutboundDetailsByItem(itemId: Int): List<OutboundDetailsEntity>
    
    @Query("SELECT * FROM outbounds WHERE id = :outboundId")
    suspend fun getOutboundById(outboundId: Long): OutboundEntity?
}
