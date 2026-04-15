package com.example.stock.Domain.repository

import com.example.stock.Domain.model.Customer
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {
    fun getAllCustomers(): Flow<List<Customer>>
    suspend fun insertCustomer(customer: Customer)
    suspend fun getCustomerById(id: Int): Customer?
}
