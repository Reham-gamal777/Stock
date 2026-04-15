package com.example.stock.Domain.usecase

import com.example.stock.Domain.model.Outbound
import com.example.stock.Domain.model.OutboundDetails
import com.example.stock.Domain.repository.CustomerRepository
import com.example.stock.Domain.repository.OutboundRepository
import javax.inject.Inject

class AddOutboundUseCase @Inject constructor(
    private val outboundRepository: OutboundRepository,
    private val customerRepository: CustomerRepository
) {
    suspend operator fun invoke(outbound: Outbound, details: List<OutboundDetails>) {
        // 1. تسجيل الفاتورة
        outboundRepository.insertOutbound(outbound, details)

        // 2. تحديث مديونية العميل (منطق العمل)
        val customer = customerRepository.getCustomerById(outbound.customerId)
        customer?.let {
            val totalInvoice = details.sumOf { d -> d.amount * d.price }
            val remainingOnCustomer = totalInvoice - outbound.moneyReceived
            customerRepository.insertCustomer(
                it.copy(customerDebt = it.customerDebt + remainingOnCustomer)
            )
        }
    }
}
