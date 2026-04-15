package com.example.stock.presentation.stock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stock.Domain.repository.*
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

data class StockMovementResult(
    val movements: List<ItemMovementUiModel>,
    val finalBalance: Int
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
    private val itemRepository: ItemRepository,
    private val stockRepository: StockRepository,
    private val inboundRepository: InboundRepository,
    private val outboundRepository: OutboundRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StockState())
    val state: StateFlow<StockState> = _state.asStateFlow()

    init {
        loadStockData()
    }

    fun loadStockData() {
        viewModelScope.launch {
            itemRepository.getAllItems().collectLatest { items ->
                val uiModels = items.map { item ->
                    val inTotal = stockRepository.getItemInboundDetails(item.id).sumOf { it.amount }
                    val outTotal = stockRepository.getItemOutboundDetails(item.id).sumOf { it.amount }
                    val returnedTotal = stockRepository.getItemReturnedDetails(item.id).sumOf { it.amount }
                    
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
            val inDetails = stockRepository.getItemInboundDetails(itemId)
            val outDetails = stockRepository.getItemOutboundDetails(itemId)
            val retDetails = stockRepository.getItemReturnedDetails(itemId)
            
            var runningBalance = 0
            
            inDetails.forEach {
                runningBalance += it.amount
                val data = inboundRepository.getInboundById(it.inboundId)
                movements.add(ItemMovementUiModel(data?.date ?: "-", "شراء (${data?.invoiceNumber ?: "#"})", it.amount, null, runningBalance))
            }
            
            retDetails.forEach {
                runningBalance += it.amount
                movements.add(ItemMovementUiModel("مرتجع", "مرتجع من عميل", it.amount, null, runningBalance))
            }

            outDetails.forEach {
                runningBalance -= it.amount
                val data = outboundRepository.getOutboundById(it.outboundId.toLong())
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
            itemRepository.insertItem(com.example.stock.Domain.model.Item(itemName = name, itemNum = 0))
            loadStockData()
        }
    }
}
