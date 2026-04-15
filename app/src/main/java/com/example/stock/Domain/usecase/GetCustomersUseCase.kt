package com.example.stock.Domain.usecase

import com.example.stock.Domain.model.Customer
import com.example.stock.Domain.repository.CustomerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCustomersUseCase @Inject constructor(
    private val repository: CustomerRepository
) {
    operator fun invoke(): Flow<List<Customer>> {
        return repository.getAllCustomers()
    }
}
