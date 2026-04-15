package com.example.stock.data.repository

import com.example.stock.Domain.model.Stock
import com.example.stock.Domain.model.InboundDetails
import com.example.stock.Domain.model.OutboundDetails
import com.example.stock.Domain.model.ReturnedDetails
import com.example.stock.Domain.repository.StockRepository
import com.example.stock.data.local.dao.InboundDao
import com.example.stock.data.local.dao.OutboundDao
import com.example.stock.data.local.dao.ReturnedDao
import com.example.stock.data.local.dao.StockDao
import com.example.stock.data.mapper.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class StockRepositoryImpl @Inject constructor(
    private val stockDao: StockDao,
    private val inboundDao: InboundDao,
    private val outboundDao: OutboundDao,
    private val returnedDao: ReturnedDao
) : StockRepository {
    
    override fun getAllStock(): Flow<List<Stock>> = 
        stockDao.getAllStock().map { entities -> entities.map { it.toStock() } }
    
    override suspend fun insertStock(stock: Stock) = 
        stockDao.insertStock(stock.toStockEntity())
    
    override suspend fun getItemInboundDetails(itemId: Int): List<InboundDetails> = 
         inboundDao.getInboundDetailsByItem(itemId).map { it.toInboundDetails() }
        
    override suspend fun getItemOutboundDetails(itemId: Int): List<OutboundDetails> = 
        outboundDao.getOutboundDetailsByItem(itemId).map { it.toOutboundDetails() }

    override suspend fun getItemReturnedDetails(itemId: Int): List<ReturnedDetails> =
        returnedDao.getReturnedDetailsByItem(itemId).map { it.toReturnedDetails() }
}
