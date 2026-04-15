package com.example.stock.presentation.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stock.Domain.model.Customer
import com.example.stock.Domain.model.CustomerTransactionUiModel
import com.example.stock.Domain.usecase.AddCustomerUseCase
import com.example.stock.Domain.usecase.GetCustomerDetailsUseCase
import com.example.stock.Domain.usecase.GetCustomersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

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
    private val getCustomersUseCase: GetCustomersUseCase,
    private val addCustomerUseCase: AddCustomerUseCase,
    private val getCustomerDetailsUseCase: GetCustomerDetailsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CustomerState())
    val state: StateFlow<CustomerState> = _state.asStateFlow()

    init {
        loadCustomers()
    }

    private fun loadCustomers() {
        viewModelScope.launch {
            getCustomersUseCase().collectLatest { customers ->
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

    fun addCustomer(name: String, phone: String, debt: Double) {
        viewModelScope.launch {
            addCustomerUseCase(name, phone, debt)
        }
    }

    fun loadCustomerDetails(customer: Customer) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, selectedCustomer = customer)
            val result = getCustomerDetailsUseCase(customer)
            _state.value = _state.value.copy(
                transactions = result.transactions,
                totalPurchases = result.totalPurchases,
                totalPayments = result.totalPayments,
                isLoading = false
            )
        }
    }
}
