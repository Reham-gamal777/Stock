package com.example.stock.data.repository

import com.example.stock.Domain.model.Inbound
import com.example.stock.Domain.model.InboundDetails
import com.example.stock.Domain.repository.InboundRepository
import com.example.stock.data.local.dao.InboundDao
import com.example.stock.data.mapper.toInbound
import com.example.stock.data.mapper.toInboundDetails
import com.example.stock.data.mapper.toInboundDetailsEntity
import com.example.stock.data.mapper.toInboundEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class InboundRepositoryImpl @Inject constructor(
    private val dao: InboundDao
) : InboundRepository {
    override fun getAllInbounds(): Flow<List<Inbound>> = dao.getAllInbounds().map { it.map { e -> e.toInbound() } }
    override suspend fun getInboundDetails(inboundId: Int): List<InboundDetails> = dao.getDetailsForInbound(inboundId).map { it.toInboundDetails() }
    override suspend fun insertInbound(inbound: Inbound, details: List<InboundDetails>) {
        val id = dao.insertInbound(inbound.toInboundEntity())
        dao.insertInboundDetails(details.map { it.copy(inboundId = id.toInt()).toInboundDetailsEntity() })
    }
    override suspend fun getInboundById(id: Int): Inbound? = dao.getInboundById(id)?.toInbound()
}
