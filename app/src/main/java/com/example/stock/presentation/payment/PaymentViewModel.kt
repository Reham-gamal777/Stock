package com.example.stock.presentation.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stock.Domain.model.Customer
import com.example.stock.Domain.model.Payment
import com.example.stock.Domain.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class PaymentUiModel(
    val payment: Payment,
    val customerName: String
)

data class PaymentState(
    val payments: List<PaymentUiModel> = emptyList(),
    val filteredPayments: List<PaymentUiModel> = emptyList(),
    val allCustomers: List<Customer> = emptyList(),
    val selectedCustomer: Customer? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false
)

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val repository: StockRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PaymentState())
    val state: StateFlow<PaymentState> = _state.asStateFlow()

    init {
        loadPayments()
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            repository.getAllCustomers().collectLatest { customers ->
                _state.value = _state.value.copy(allCustomers = customers)
            }
        }
    }

    private fun loadPayments() {
        viewModelScope.launch {
            repository.getAllPayments().collectLatest { payments ->
                val uiModels = payments.map { payment ->
                    val customer = repository.getCustomerById(payment.customerId)
                    PaymentUiModel(payment, customer?.customerName ?: "عميل غير معروف")
                }
                _state.value = _state.value.copy(
                    payments = uiModels,
                    filteredPayments = filterPayments(_state.value.searchQuery, uiModels)
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(
            searchQuery = query,
            filteredPayments = filterPayments(query, _state.value.payments)
        )
    }

    private fun filterPayments(query: String, list: List<PaymentUiModel>): List<PaymentUiModel> {
        return if (query.isEmpty()) list else list.filter {
            it.customerName.contains(query, ignoreCase = true)
        }
    }

    fun selectCustomer(customer: Customer) {
        _state.value = _state.value.copy(selectedCustomer = customer)
    }

    fun savePayment(amount: Double, type: String) {
        val customer = _state.value.selectedCustomer ?: return
        viewModelScope.launch {
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
            val payment = Payment(
                customerId = customer.id,
                amount = amount,
                paymentType = type,
                date = date
            )
            repository.insertPayment(payment)
            _state.value = _state.value.copy(selectedCustomer = null)
        }
    }
}
