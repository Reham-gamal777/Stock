package com.example.stock.Domain.repository

import com.example.stock.Domain.model.Outbound
import com.example.stock.Domain.model.OutboundDetails
import kotlinx.coroutines.flow.Flow

interface OutboundRepository {
    fun getAllOutbounds(): Flow<List<Outbound>>
    suspend fun getOutboundsForCustomer(customerId: Int): List<Outbound>
    suspend fun getOutboundDetails(outboundId: Long): List<OutboundDetails>
    suspend fun insertOutbound(outbound: Outbound, details: List<OutboundDetails>)
    suspend fun getOutboundById(id: Long): Outbound?
}
