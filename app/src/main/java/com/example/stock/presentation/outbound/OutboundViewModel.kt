package com.example.stock.presentation.outbound

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stock.Domain.model.*
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

data class OutboundUiModel(
    val outbound: Outbound,
    val customerName: String,
    val totalAmount: Double
)

data class OutboundDetailUiModel(
    val itemId: Int,
    val itemName: String,
    val amount: Int,
    val price: Double,
    val total: Double
)

data class OutboundState(
    val outbounds: List<OutboundUiModel> = emptyList(),
    val selectedOutbound: Outbound? = null,
    val selectedCustomer: Customer? = null,
    val selectedDetails: List<OutboundDetailUiModel> = emptyList(),
    val isLoading: Boolean = false,
    
    // Add Outbound State
    val allCustomers: List<Customer> = emptyList(),
    val allItems: List<Item> = emptyList(),
    val newOutboundItems: List<OutboundDetailUiModel> = emptyList(),
    val tempInvoiceNumber: String = ""
)

@HiltViewModel
class OutboundViewModel @Inject constructor(
    private val repository: StockRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OutboundState())
    val state: StateFlow<OutboundState> = _state.asStateFlow()

    init {
        loadOutbounds()
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            repository.getAllCustomers().collectLatest { customers ->
                _state.value = _state.value.copy(allCustomers = customers)
            }
        }
        viewModelScope.launch {
            repository.getAllItems().collectLatest { items ->
                _state.value = _state.value.copy(allItems = items)
            }
        }
    }

    private fun loadOutbounds() {
        viewModelScope.launch {
            repository.getAllOutbounds().collectLatest { outbounds ->
                val uiOutbounds = outbounds.map { outbound ->
                    val customer = repository.getCustomerById(outbound.customerId)
                    val details = repository.getOutboundDetails(outbound.id.toLong())
                    val total = details.sumOf { it.amount * it.price }
                    OutboundUiModel(
                        outbound = outbound,
                        customerName = customer?.customerName ?: "عميل غير معروف",
                        totalAmount = total
                    )
                }
                _state.value = _state.value.copy(outbounds = uiOutbounds)
            }
        }
    }

    fun onOutboundClick(outboundUi: OutboundUiModel) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val customer = repository.getCustomerById(outboundUi.outbound.customerId)
            val details = repository.getOutboundDetails(outboundUi.outbound.id.toLong())
            
            val detailUiModels = details.map { detail ->
                val item = repository.getItemById(detail.itemId)
                OutboundDetailUiModel(
                    itemId = detail.itemId,
                    itemName = item?.itemName ?: "صنف غير معروف",
                    amount = detail.amount,
                    price = detail.price,
                    total = detail.amount * detail.price
                )
            }

            _state.value = _state.value.copy(
                selectedOutbound = outboundUi.outbound,
                selectedCustomer = customer,
                selectedDetails = detailUiModels,
                isLoading = false
            )
        }
    }

    // Add Outbound Actions
    fun selectCustomer(customer: Customer) {
        _state.value = _state.value.copy(selectedCustomer = customer)
    }

    fun addItemToNewOutbound(item: Item, amount: Int, price: Double) {
        val currentItems = _state.value.newOutboundItems.toMutableList()
        currentItems.add(
            OutboundDetailUiModel(
                itemId = item.id,
                itemName = item.itemName,
                amount = amount,
                price = price,
                total = amount * price
            )
        )
        _state.value = _state.value.copy(newOutboundItems = currentItems)
    }

    fun removeDetailFromNewOutbound(detail: OutboundDetailUiModel) {
        val currentItems = _state.value.newOutboundItems.toMutableList()
        currentItems.remove(detail)
        _state.value = _state.value.copy(newOutboundItems = currentItems)
    }

    fun saveOutbound(invoiceNumber: String, moneyReceived: Int) {
        val currentState = _state.value
        val customer = currentState.selectedCustomer ?: return
        if (currentState.newOutboundItems.isEmpty()) return

        viewModelScope.launch {
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val outbound = Outbound(
                userId = "user_1", // Mock user
                customerId = customer.id,
                invoiceNumber = invoiceNumber,
                image = "",
                outboundDate = date,
                latitude = 0.0,
                longitude = 0.0,
                moneyReceived = moneyReceived
            )
            
            val details = currentState.newOutboundItems.map {
                OutboundDetails(
                    outboundId = 0, // Will be set by repository
                    itemId = it.itemId,
                    amount = it.amount,
                    price = it.price
                )
            }
            
            repository.insertOutbound(outbound, details)
            // Clear state after save
            _state.value = _state.value.copy(
                selectedCustomer = null,
                newOutboundItems = emptyList()
            )
        }
    }
    
    fun clearSelection() {
        _state.value = _state.value.copy(selectedOutbound = null, selectedCustomer = null, selectedDetails = emptyList())
    }
}
