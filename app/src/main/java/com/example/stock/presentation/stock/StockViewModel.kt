package com.example.stock.presentation.stock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stock.Domain.model.Item
import com.example.stock.Domain.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StockItemUiModel(
    val id: Int,
    val itemName: String,
    val balance: Int
)

data class ItemMovementUiModel(
    val date: String,
    val operation: String,
    val inbound: Int?,
    val outbound: Int?,
    val balanceAfter: Int
)

data class StockState(
    val stockItems: List<StockItemUiModel> = emptyList(),
    val filteredStockItems: List<StockItemUiModel> = emptyList(),
    val searchQuery: String = "",
    val selectedItemName: String = "",
    val selectedItemBalance: Int = 0,
    val itemMovements: List<ItemMovementUiModel> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class StockViewModel @Inject constructor(
    private val repository: StockRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StockState())
    val state: StateFlow<StockState> = _state.asStateFlow()

    init {
        loadStockData()
    }

    fun loadStockData() {
        viewModelScope.launch {
            repository.getAllItems().collectLatest { items ->
                val uiModels = items.map { item ->
                    val inTotal = repository.getItemInboundDetails(item.id).sumOf { it.amount }
                    val outTotal = repository.getItemOutboundDetails(item.id).sumOf { it.amount }
                    val returnedTotal = repository.getItemReturnedDetails(item.id).sumOf { it.amount }
                    
                    // المعادلة: وارد - صادر + مرتجع عملاء
                    val balance = inTotal - outTotal + returnedTotal
                    StockItemUiModel(item.id, item.itemName, balance)
                }
                _state.value = _state.value.copy(
                    stockItems = uiModels,
                    filteredStockItems = filterStock(uiModels, _state.value.searchQuery)
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(
            searchQuery = query,
            filteredStockItems = filterStock(_state.value.stockItems, query)
        )
    }

    private fun filterStock(list: List<StockItemUiModel>, query: String): List<StockItemUiModel> {
        return if (query.isEmpty()) list else list.filter { it.itemName.contains(query, ignoreCase = true) }
    }

    fun loadItemMovements(itemId: Int, itemName: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, selectedItemName = itemName)
            
            val movements = mutableListOf<ItemMovementUiModel>()
            val inDetails = repository.getItemInboundDetails(itemId)
            val outDetails = repository.getItemOutboundDetails(itemId)
            val retDetails = repository.getItemReturnedDetails(itemId)
            
            var runningBalance = 0
            
            // 1. إضافة الواردات
            inDetails.forEach {
                runningBalance += it.amount
                val data = repository.getInboundById(it.inboundId)
                movements.add(ItemMovementUiModel(data?.date ?: "-", "شراء (${data?.invoiceNumber ?: "#"})", it.amount, null, runningBalance))
            }
            
            // 2. إضافة المرتجعات (تزيد المخزن)
            retDetails.forEach {
                runningBalance += it.amount
                movements.add(ItemMovementUiModel("مرتجع", "مرتجع من عميل", it.amount, null, runningBalance))
            }

            // 3. إضافة الصادرات (تنقص المخزن)
            outDetails.forEach {
                runningBalance -= it.amount
                val data = repository.getOutboundById(it.outboundId.toLong())
                movements.add(ItemMovementUiModel(data?.outboundDate?.take(10) ?: "-", "بيع (${data?.invoiceNumber ?: "#"})", null, it.amount, runningBalance))
            }

            _state.value = _state.value.copy(
                itemMovements = movements.sortedByDescending { it.date },
                selectedItemBalance = runningBalance,
                isLoading = false
            )
        }
    }

    fun addNewItem(name: String) {
        viewModelScope.launch {
            repository.insertItem(Item(itemName = name, itemNum = 0))
            loadStockData()
        }
    }
}

