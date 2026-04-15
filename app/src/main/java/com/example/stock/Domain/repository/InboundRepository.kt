package com.example.stock.Domain.repository

import com.example.stock.Domain.model.Inbound
import com.example.stock.Domain.model.InboundDetails
import kotlinx.coroutines.flow.Flow

interface InboundRepository {
    fun getAllInbounds(): Flow<List<Inbound>>
    suspend fun getInboundDetails(inboundId: Int): List<InboundDetails>
    suspend fun insertInbound(inbound: Inbound, details: List<InboundDetails>)
    suspend fun getInboundById(id: Int): Inbound?
}
