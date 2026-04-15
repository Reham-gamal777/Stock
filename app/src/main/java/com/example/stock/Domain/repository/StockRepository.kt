package com.example.stock.Domain.repository

import com.example.stock.Domain.model.InboundDetails
import com.example.stock.Domain.model.OutboundDetails
import com.example.stock.Domain.model.ReturnedDetails
import com.example.stock.Domain.model.Stock
import kotlinx.coroutines.flow.Flow

interface StockRepository {
    fun getAllStock(): Flow<List<Stock>>
    suspend fun insertStock(stock: Stock)
    suspend fun getItemInboundDetails(itemId: Int): List<InboundDetails>
    suspend fun getItemOutboundDetails(itemId: Int): List<OutboundDetails>
    suspend fun getItemReturnedDetails(itemId: Int): List<ReturnedDetails>
}
