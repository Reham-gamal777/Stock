package com.example.stock.Domain.usecase

import com.example.stock.Domain.model.Outbound
import com.example.stock.Domain.model.OutboundDetails
import com.example.stock.Domain.repository.CustomerRepository
import com.example.stock.Domain.repository.OutboundRepository
import com.example.stock.Domain.repository.ItemRepository
import javax.inject.Inject

class AddOutboundUseCase @Inject constructor(
    private val outboundRepository: OutboundRepository,
    private val customerRepository: CustomerRepository
) {
    suspend operator fun invoke(outbound: Outbound, details: List<OutboundDetails>) {
        // 1. تسجيل عملية البيع
        outboundRepository.insertOutbound(outbound, details)

        // 2. حساب إجمالي الفاتورة وتحديث مديونية العميل
        val totalInvoice = details.sumOf { it.amount * it.price }
        val remainingAmount = totalInvoice - outbound.moneyReceived
        
        val customer = customerRepository.getCustomerById(outbound.customerId)
        customer?.let {
            customerRepository.insertCustomer(
                it.copy(customerDebt = it.customerDebt + remainingAmount)
            )
        }
        
        // ملاحظة: الرصيد في المخزن يتم حسابه تلقائياً في GetStockBalanceUseCase 
        // بناءً على (إجمالي الوارد - إجمالي الصادر) لضمان الدقة المحاسبية.
    }
}
