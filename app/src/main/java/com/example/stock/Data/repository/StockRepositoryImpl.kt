package com.example.stock.data.repository

import com.example.stock.Domain.model.*
import com.example.stock.Domain.repository.StockRepository
import com.example.stock.data.local.dao.StockDao
import com.example.stock.data.mapper.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class StockRepositoryImpl @Inject constructor(
    private val dao: StockDao
) : StockRepository {

    override fun getAllItems(): Flow<List<Item>> {
        return dao.getAllItems().map { entities -> entities.map { it.toItem() } }
    }

    override suspend fun insertItem(item: Item) {
        dao.insertItem(item.toItemEntity())
    }

    override suspend fun getItemById(id: Int): Item? {
        return dao.getItemById(id)?.toItem()
    }

    override fun getAllCustomers(): Flow<List<Customer>> {
        return dao.getAllCustomers().map { entities -> entities.map { it.toCustomer() } }
    }

    override suspend fun insertCustomer(customer: Customer) {
        dao.insertCustomer(customer.toCustomerEntity())
    }

    override suspend fun getCustomerById(id: Int): Customer? {
        return dao.getCustomerById(id)?.toCustomer()
    }

    override fun getAllStock(): Flow<List<Stock>> {
        return dao.getAllStock().map { entities -> entities.map { it.toStock() } }
    }

    override suspend fun insertStock(stock: Stock) {
        dao.insertStock(stock.toStockEntity())
    }

    override fun getAllOutbounds(): Flow<List<Outbound>> {
        return dao.getAllOutbounds().map { entities -> entities.map { it.toOutbound() } }
    }

    override suspend fun getOutboundsForCustomer(customerId: Int): List<Outbound> {
        return dao.getOutboundsForCustomer(customerId).map { it.toOutbound() }
    }

    override suspend fun getOutboundDetails(outboundId: Long): List<OutboundDetails> {
        return dao.getDetailsForOutbound(outboundId).map { it.toOutboundDetails() }
    }

    override suspend fun insertOutbound(outbound: Outbound, details: List<OutboundDetails>) {
        // 1. حفظ الفاتورة وتفاصيلها
        val id = dao.insertOutbound(outbound.toOutboundEntity())
        dao.insertOutboundDetails(details.map { it.copy(outboundId = id).toOutboundDetailsEntity() })

        // 2. تحديث مديونية العميل
        val customer = dao.getCustomerById(outbound.customerId)
        customer?.let {
            val totalInvoice = details.sumOf { d -> d.amount * d.price }
            val remainingOnCustomer = totalInvoice - outbound.moneyReceived
            val updatedCustomer = it.copy(customerDebt = it.customerDebt + remainingOnCustomer)
            dao.insertCustomer(updatedCustomer) // تحديث البيانات
        }
    }

    override fun getAllInbounds(): Flow<List<Inbound>> {
        return dao.getAllInbounds().map { entities -> entities.map { it.toInbound() } }
    }

    override suspend fun getInboundDetails(inboundId: Int): List<InboundDetails> {
        return dao.getDetailsForInbound(inboundId).map { it.toInboundDetails() }
    }

    override suspend fun insertInbound(inbound: Inbound, details: List<InboundDetails>) {
        val id = dao.insertInbound(inbound.toInboundEntity())
        dao.insertInboundDetails(details.map { it.copy(inboundId = id.toInt()).toInboundDetailsEntity() })
    }

    override suspend fun getItemInboundDetails(itemId: Int): List<InboundDetails> {
        return dao.getInboundDetailsByItem(itemId).map { it.toInboundDetails() }
    }

    override suspend fun getItemOutboundDetails(itemId: Int): List<OutboundDetails> {
        return dao.getOutboundDetailsByItem(itemId).map { it.toOutboundDetails() }
    }

    override suspend fun getInboundById(id: Int): Inbound? {
        return dao.getInboundById(id)?.toInbound()
    }

    override suspend fun getOutboundById(id: Long): Outbound? {
        return dao.getOutboundById(id)?.toOutbound()
    }

    override fun getAllReturned(): Flow<List<Returned>> {
        return dao.getAllReturned().map { entities -> entities.map { it.toReturned() } }
    }

    override suspend fun getReturnedDetails(returnedId: Int): List<ReturnedDetails> {
        return dao.getDetailsForReturned(returnedId).map { it.toReturnedDetails() }
    }

    override suspend fun insertReturned(returned: Returned, details: List<ReturnedDetails>) {
        // 1. حفظ المرتجع
        val id = dao.insertReturned(returned.toReturnedEntity())
        dao.insertReturnedDetails(details.map { it.copy(returnedId = id.toInt()).toReturnedDetailsEntity() })

        // 2. تقليل مديونية العميل بقيمة المرتجع
        val customer = dao.getCustomerById(returned.customerId)
        customer?.let {
            val totalReturned = details.sumOf { d -> d.amount * d.price }
            val updatedCustomer = it.copy(customerDebt = it.customerDebt - totalReturned)
            dao.insertCustomer(updatedCustomer)
        }
    }

    override suspend fun getItemReturnedDetails(itemId: Int): List<ReturnedDetails> {
        return dao.getReturnedDetailsByItem(itemId).map { it.toReturnedDetails() }
    }

    override fun getAllPayments(): Flow<List<Payment>> {
        return dao.getAllPayments().map { entities -> entities.map { it.toPayment() } }
    }

    override suspend fun getPaymentsForCustomer(customerId: Int): List<Payment> {
        return dao.getPaymentsForCustomer(customerId).map { it.toPayment() }
    }

    override suspend fun insertPayment(payment: Payment) {
        // 1. حفظ عملية الدفع
        dao.insertPayment(payment.toPaymentEntity())

        // 2. خصم المبلغ من مديونية العميل
        val customer = dao.getCustomerById(payment.customerId)
        customer?.let {
            val updatedCustomer = it.copy(customerDebt = it.customerDebt - payment.amount)
            dao.insertCustomer(updatedCustomer)
        }
    }
}
