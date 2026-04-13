package com.example.stock.presentation.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stock.Domain.model.Customer
import com.example.stock.Domain.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CustomerTransactionUiModel(
    val date: String,
    val type: String,
    val amount: Double,
    val representative: String = "-",
    val isPayment: Boolean
)

data class CustomerState(
    val customers: List<Customer> = emptyList(),
    val filteredCustomers: List<Customer> = emptyList(),
    val selectedCustomer: Customer? = null,
    val transactions: List<CustomerTransactionUiModel> = emptyList(),
    val totalPurchases: Double = 0.0,
    val totalPayments: Double = 0.0,
    val searchQuery: String = "",
    val isLoading: Boolean = false
)

@HiltViewModel
class CustomerViewModel @Inject constructor(
    private val repository: StockRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CustomerState())
    val state: StateFlow<CustomerState> = _state.asStateFlow()

    init {
        loadCustomers()
    }

    private fun loadCustomers() {
        viewModelScope.launch {
            repository.getAllCustomers().collectLatest { customers ->
                _state.value = _state.value.copy(
                    customers = customers,
                    filteredCustomers = filterCustomers(_state.value.searchQuery, customers)
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(
            searchQuery = query,
            filteredCustomers = filterCustomers(query, _state.value.customers)
        )
    }

    private fun filterCustomers(query: String, list: List<Customer>): List<Customer> {
        return if (query.isEmpty()) list else list.filter {
            it.customerName.contains(query, ignoreCase = true) || it.customerNum.contains(query)
        }
    }

    fun loadCustomerDetails(customer: Customer) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, selectedCustomer = customer)
            
            val outbounds = repository.getOutboundsForCustomer(customer.id)
            val payments = repository.getPaymentsForCustomer(customer.id)
            
            val transactionsList = mutableListOf<CustomerTransactionUiModel>()
            var purchasesSum = 0.0
            var paymentsSum = 0.0

            outbounds.forEach { outbound ->
                val details = repository.getOutboundDetails(outbound.id.toLong())
                val total = details.sumOf { it.amount * it.price }
                purchasesSum += total
                transactionsList.add(CustomerTransactionUiModel(
                    date = outbound.outboundDate,
                    type = "فاتورة صادر (${outbound.invoiceNumber})",
                    amount = total,
                    representative = outbound.userId, 
                    isPayment = false
                ))
            }

            payments.forEach { payment ->
                paymentsSum += payment.amount
                transactionsList.add(CustomerTransactionUiModel(
                    date = payment.date,
                    type = "تحصيل مالي (${payment.paymentType})",
                    amount = payment.amount,
                    isPayment = true
                ))
            }

            _state.value = _state.value.copy(
                transactions = transactionsList.sortedByDescending { it.date },
                totalPurchases = purchasesSum,
                totalPayments = paymentsSum,
                isLoading = false
            )
        }
    }

    fun addCustomer(name: String, phone: String, debt: Double) {
        viewModelScope.launch {
            repository.insertCustomer(Customer(userId = "admin", customerName = name, customerNum = phone, customerDebt = debt))
        }
    }
}
