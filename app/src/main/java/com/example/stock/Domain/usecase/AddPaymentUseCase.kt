package com.example.stock.Domain.usecase

import com.example.stock.Domain.model.Payment
import com.example.stock.Domain.repository.CustomerRepository
import com.example.stock.Domain.repository.PaymentRepository
import javax.inject.Inject

class AddPaymentUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val customerRepository: CustomerRepository
) {
    suspend operator fun invoke(payment: Payment) {
        // 1. حفظ العملية
        paymentRepository.insertPayment(payment)

        // 2. تحديث المديونية (خصم المبلغ)
        val customer = customerRepository.getCustomerById(payment.customerId)
        customer?.let {
            customerRepository.insertCustomer(
                it.copy(customerDebt = it.customerDebt - payment.amount)
            )
        }
    }
}
