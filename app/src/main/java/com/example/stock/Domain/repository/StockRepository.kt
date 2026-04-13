package com.example.stock.Domain.repository

import com.example.stock.Domain.model.*
import kotlinx.coroutines.flow.Flow

interface StockRepository {
    fun getAllItems(): Flow<List<Item>>
    suspend fun insertItem(item: Item)
    suspend fun getItemById(id: Int): Item?

    fun getAllCustomers(): Flow<List<Customer>>
    suspend fun insertCustomer(customer: Customer)
    suspend fun getCustomerById(id: Int): Customer?

    fun getAllStock(): Flow<List<Stock>>
    suspend fun insertStock(stock: Stock)

    fun getAllOutbounds(): Flow<List<Outbound>>
    suspend fun getOutboundsForCustomer(customerId: Int): List<Outbound>
    suspend fun getOutboundDetails(outboundId: Long): List<OutboundDetails>
    suspend fun insertOutbound(outbound: Outbound, details: List<OutboundDetails>)

    // Inbound
    fun getAllInbounds(): Flow<List<Inbound>>
    suspend fun getInboundDetails(inboundId: Int): List<InboundDetails>
    suspend fun insertInbound(inbound: Inbound, details: List<InboundDetails>)
    
    // Inventory/Stock specific
    suspend fun getItemInboundDetails(itemId: Int): List<InboundDetails>
    suspend fun getItemOutboundDetails(itemId: Int): List<OutboundDetails>
    suspend fun getInboundById(id: Int): Inbound?
    suspend fun getOutboundById(id: Long): Outbound?

    // Returned
    fun getAllReturned(): Flow<List<Returned>>
    suspend fun getReturnedDetails(returnedId: Int): List<ReturnedDetails>
    suspend fun insertReturned(returned: Returned, details: List<ReturnedDetails>)
    suspend fun getItemReturnedDetails(itemId: Int): List<ReturnedDetails>

    // Payments
    fun getAllPayments(): Flow<List<Payment>>
    suspend fun getPaymentsForCustomer(customerId: Int): List<Payment>
    suspend fun insertPayment(payment: Payment)
}
