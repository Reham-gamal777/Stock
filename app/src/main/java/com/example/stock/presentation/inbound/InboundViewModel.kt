package com.example.stock.presentation.inbound

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stock.Domain.model.*
import com.example.stock.Domain.repository.InboundRepository
import com.example.stock.Domain.repository.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class InboundUiModel(
    val inbound: Inbound,
    val totalItems: Int
)

data class InboundDetailUiModel(
    val itemId: Int,
    val itemName: String,
    val amount: Int
)

data class InboundState(
    val inbounds: List<InboundUiModel> = emptyList(),
    val filteredInbounds: List<InboundUiModel> = emptyList(),
    val selectedInbound: Inbound? = null,
    val selectedDetails: List<InboundDetailUiModel> = emptyList(),
    val allItems: List<Item> = emptyList(),
    val newInboundItems: List<InboundDetailUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = ""
)

@HiltViewModel
class InboundViewModel @Inject constructor(
    private val inboundRepository: InboundRepository,
    private val itemRepository: ItemRepository
) : ViewModel() {

    private val _state = MutableStateFlow(InboundState())
    val state: StateFlow<InboundState> = _state.asStateFlow()

    init {
        loadInbounds()
        loadItems()
    }

    private fun loadItems() {
        viewModelScope.launch {
            itemRepository.getAllItems().collectLatest { items ->
                _state.value = _state.value.copy(allItems = items)
            }
        }
    }

    private fun loadInbounds() {
        viewModelScope.launch {
            inboundRepository.getAllInbounds().collectLatest { inbounds ->
                val uiInbounds = inbounds.map { inbound ->
                    val details = inboundRepository.getInboundDetails(inbound.id)
                    InboundUiModel(inbound, details.sumOf { it.amount })
                }
                _state.value = _state.value.copy(
                    inbounds = uiInbounds,
                    filteredInbounds = filterInbounds(_state.value.searchQuery, uiInbounds)
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(
            searchQuery = query,
            filteredInbounds = filterInbounds(query, _state.value.inbounds)
        )
    }

    private fun filterInbounds(query: String, list: List<InboundUiModel>): List<InboundUiModel> {
        return if (query.isEmpty()) {
            list
        } else {
            list.filter {
                it.inbound.invoiceNumber.contains(query, ignoreCase = true) ||
                it.inbound.supplierName.contains(query, ignoreCase = true)
            }
        }
    }

    fun onInboundClick(inboundUi: InboundUiModel) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val details = inboundRepository.getInboundDetails(inboundUi.inbound.id)
            val detailUiModels = details.map { detail ->
                val item = itemRepository.getItemById(detail.itemId)
                InboundDetailUiModel(detail.itemId, item?.itemName ?: "غير معروف", detail.amount)
            }
            _state.value = _state.value.copy(
                selectedInbound = inboundUi.inbound,
                selectedDetails = detailUiModels,
                isLoading = false
            )
        }
    }

    fun addItemToNewInbound(item: Item, amount: Int) {
        val current = _state.value.newInboundItems.toMutableList()
        current.add(InboundDetailUiModel(item.id, item.itemName, amount))
        _state.value = _state.value.copy(newInboundItems = current)
    }

    fun removeDetailFromNewInbound(detail: InboundDetailUiModel) {
        val current = _state.value.newInboundItems.toMutableList()
        current.remove(detail)
        _state.value = _state.value.copy(newInboundItems = current)
    }

    fun saveInbound(invoiceNumber: String, supplierName: String) {
        viewModelScope.launch {
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val inbound = Inbound(invoiceNumber = invoiceNumber, supplierName = supplierName, date = date)
            val details = _state.value.newInboundItems.map {
                InboundDetails(inboundId = 0, itemId = it.itemId, amount = it.amount)
            }
            inboundRepository.insertInbound(inbound, details)
            _state.value = _state.value.copy(newInboundItems = emptyList())
        }
    }
}
