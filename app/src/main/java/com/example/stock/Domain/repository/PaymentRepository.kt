package com.example.stock.Domain.repository

import com.example.stock.Domain.model.Payment
import kotlinx.coroutines.flow.Flow

interface PaymentRepository {
    fun getAllPayments(): Flow<List<Payment>>
    suspend fun getPaymentsForCustomer(customerId: Int): List<Payment>
    suspend fun insertPayment(payment: Payment)
}
