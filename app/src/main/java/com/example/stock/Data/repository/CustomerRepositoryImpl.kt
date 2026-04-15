package com.example.stock.data.repository

import com.example.stock.Domain.model.Customer
import com.example.stock.Domain.repository.CustomerRepository
import com.example.stock.data.local.dao.CustomerDao
import com.example.stock.data.mapper.toCustomer
import com.example.stock.data.mapper.toCustomerEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CustomerRepositoryImpl @Inject constructor(
    private val dao: CustomerDao
) : CustomerRepository {
    override fun getAllCustomers(): Flow<List<Customer>> = dao.getAllCustomers().map { it.map { e -> e.toCustomer() } }
    override suspend fun insertCustomer(customer: Customer) = dao.insertCustomer(customer.toCustomerEntity())
    override suspend fun getCustomerById(id: Int): Customer? = dao.getCustomerById(id)?.toCustomer()
}
