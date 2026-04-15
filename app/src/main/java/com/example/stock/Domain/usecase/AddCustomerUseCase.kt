package com.example.stock.Domain.usecase

import com.example.stock.Domain.model.Customer
import com.example.stock.Domain.repository.CustomerRepository
import javax.inject.Inject

class AddCustomerUseCase @Inject constructor(
    private val repository: CustomerRepository
) {
    suspend operator fun invoke(name: String, phone: String, debt: Double) {
        val customer = Customer(
            userId = "admin", // Default user for now
            customerName = name,
            customerNum = phone,
            customerDebt = debt
        )
        repository.insertCustomer(customer)
    }
}
