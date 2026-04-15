package com.example.stock.data.repository

import com.example.stock.Domain.model.Payment
import com.example.stock.Domain.repository.PaymentRepository
import com.example.stock.data.local.dao.PaymentDao
import com.example.stock.data.mapper.toPayment
import com.example.stock.data.mapper.toPaymentEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor(
    private val dao: PaymentDao
) : PaymentRepository {
    override fun getAllPayments(): Flow<List<Payment>> = dao.getAllPayments().map { it.map { e -> e.toPayment() } }
    override suspend fun getPaymentsForCustomer(customerId: Int): List<Payment> = dao.getPaymentsForCustomer(customerId).map { it.toPayment() }
    override suspend fun insertPayment(payment: Payment) = dao.insertPayment(payment.toPaymentEntity())
}
