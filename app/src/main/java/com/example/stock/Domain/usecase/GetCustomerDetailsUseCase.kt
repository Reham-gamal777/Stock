package com.example.stock.Domain.usecase

import com.example.stock.Domain.model.Customer
import com.example.stock.Domain.model.CustomerTransactionUiModel
import com.example.stock.Domain.repository.OutboundRepository
import com.example.stock.Domain.repository.PaymentRepository
import javax.inject.Inject

data class CustomerDetailsResult(
    val transactions: List<CustomerTransactionUiModel>,
    val totalPurchases: Double,
    val totalPayments: Double
)

class GetCustomerDetailsUseCase @Inject constructor(
    private val outboundRepository: OutboundRepository,
    private val paymentRepository: PaymentRepository
) {
    suspend operator fun invoke(customer: Customer): CustomerDetailsResult {
        val outbounds = outboundRepository.getOutboundsForCustomer(customer.id)
        val payments = paymentRepository.getPaymentsForCustomer(customer.id)
        
        val transactionsList = mutableListOf<CustomerTransactionUiModel>()
        var purchasesSum = 0.0
        var paymentsSum = 0.0

        outbounds.forEach { outbound ->
            val details = outboundRepository.getOutboundDetails(outbound.id.toLong())
            val total = details.sumOf { it.amount * it.price }
            purchasesSum += total
            transactionsList.add(
                CustomerTransactionUiModel(
                    date = outbound.outboundDate,
                    type = "فاتورة صادر (${outbound.invoiceNumber})",
                    amount = total,
                    representative = outbound.userId, 
                    isPayment = false
                )
            )
        }

        payments.forEach { payment ->
            paymentsSum += payment.amount
            transactionsList.add(
                CustomerTransactionUiModel(
                    date = payment.date,
                    type = "تحصيل مالي (${payment.paymentType})",
                    amount = payment.amount,
                    isPayment = true
                )
            )
        }

        return CustomerDetailsResult(
            transactions = transactionsList.sortedByDescending { it.date },
            totalPurchases = purchasesSum,
            totalPayments = paymentsSum
        )
    }
}
