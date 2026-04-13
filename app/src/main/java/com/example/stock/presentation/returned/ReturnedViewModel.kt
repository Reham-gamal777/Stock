package com.example.stock.presentation.returned

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

data class ReturnedUiModel(
    val returned: Returned,
    val customerName: String,
    val totalItems: Int
)

data class ReturnedDetailUiModel(
    val itemId: Int,
    val itemName: String,
    val amount: Int,
    val price: Double
)

data class ReturnedState(
    val returns: List<ReturnedUiModel> = emptyList(),
    val filteredReturns: List<ReturnedUiModel> = emptyList(),
    val selectedReturned: Returned? = null,
    val selectedDetails: List<ReturnedDetailUiModel> = emptyList(),
    val allCustomers: List<Customer> = emptyList(),
    val allItems: List<Item> = emptyList(),
    val newReturnedItems: List<ReturnedDetailUiModel> = emptyList(),
    val selectedCustomer: Customer? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false
)

@HiltViewModel
class ReturnedViewModel @Inject constructor(
    private val repository: StockRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ReturnedState())
    val state: StateFlow<ReturnedState> = _state.asStateFlow()

    init {
        loadReturns()
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

    private fun loadReturns() {
        viewModelScope.launch {
            repository.getAllReturned().collectLatest { returns ->
                val uiReturns = returns.map { returned ->
                    val customer = repository.getCustomerById(returned.customerId)
                    val details = repository.getReturnedDetails(returned.id)
                    ReturnedUiModel(returned, customer?.customerName ?: "غير معروف", details.sumOf { it.amount })
                }
                _state.value = _state.value.copy(
                    returns = uiReturns,
                    filteredReturns = filterReturns(_state.value.searchQuery, uiReturns)
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(
            searchQuery = query,
            filteredReturns = filterReturns(query, _state.value.returns)
        )
    }

    private fun filterReturns(query: String, list: List<ReturnedUiModel>): List<ReturnedUiModel> {
        return if (query.isEmpty()) list else list.filter {
            it.customerName.contains(query, ignoreCase = true)
        }
    }

    fun selectCustomer(customer: Customer) {
        _state.value = _state.value.copy(selectedCustomer = customer)
    }

    fun addItemToNewReturn(item: Item, amount: Int, price: Double) {
        val current = _state.value.newReturnedItems.toMutableList()
        current.add(ReturnedDetailUiModel(item.id, item.itemName, amount, price))
        _state.value = _state.value.copy(newReturnedItems = current)
    }

    fun removeDetailFromNewReturn(detail: ReturnedDetailUiModel) {
        val current = _state.value.newReturnedItems.toMutableList()
        current.remove(detail)
        _state.value = _state.value.copy(newReturnedItems = current)
    }

    fun saveReturned() {
        val currentState = _state.value
        val customer = currentState.selectedCustomer ?: return
        if (currentState.newReturnedItems.isEmpty()) return

        viewModelScope.launch {
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val returned = Returned(
                customerId = customer.id,
                returnedDate = date,
                userId = "user_1",
                latitude = 0.0,
                longitude = 0.0
            )
            
            val details = currentState.newReturnedItems.map {
                ReturnedDetails(
                    returnedId = 0,
                    itemId = it.itemId,
                    amount = it.amount,
                    price = it.price
                )
            }
            
            repository.insertReturned(returned, details)
            _state.value = _state.value.copy(
                selectedCustomer = null,
                newReturnedItems = emptyList()
            )
        }
    }

    fun onReturnedClick(returnedUi: ReturnedUiModel) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val details = repository.getReturnedDetails(returnedUi.returned.id)
            val detailUiModels = details.map { detail ->
                val item = repository.getItemById(detail.itemId)
                ReturnedDetailUiModel(detail.itemId, item?.itemName ?: "غير معروف", detail.amount, detail.price)
            }
            _state.value = _state.value.copy(
                selectedReturned = returnedUi.returned,
                selectedDetails = detailUiModels,
                isLoading = false
            )
        }
    }
}
