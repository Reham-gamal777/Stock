package com.example.stock.data.repository

import com.example.stock.Domain.model.Outbound
import com.example.stock.Domain.model.OutboundDetails
import com.example.stock.Domain.repository.OutboundRepository
import com.example.stock.data.local.dao.OutboundDao
import com.example.stock.data.mapper.toOutbound
import com.example.stock.data.mapper.toOutboundDetails
import com.example.stock.data.mapper.toOutboundDetailsEntity
import com.example.stock.data.mapper.toOutboundEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OutboundRepositoryImpl @Inject constructor(
    private val dao: OutboundDao
) : OutboundRepository {
    override fun getAllOutbounds(): Flow<List<Outbound>> = dao.getAllOutbounds().map { it.map { e -> e.toOutbound() } }
    override suspend fun getOutboundsForCustomer(customerId: Int): List<Outbound> = dao.getOutboundsForCustomer(customerId).map { it.toOutbound() }
    override suspend fun getOutboundDetails(outboundId: Long): List<OutboundDetails> = dao.getDetailsForOutbound(outboundId).map { it.toOutboundDetails() }
    override suspend fun insertOutbound(outbound: Outbound, details: List<OutboundDetails>) {
        val id = dao.insertOutbound(outbound.toOutboundEntity())
        dao.insertOutboundDetails(details.map { it.copy(outboundId = id).toOutboundDetailsEntity() })
    }
    override suspend fun getOutboundById(id: Long): Outbound? = dao.getOutboundById(id)?.toOutbound()
}
