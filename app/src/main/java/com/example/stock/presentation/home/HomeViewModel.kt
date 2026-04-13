package com.example.stock.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stock.Domain.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val outboundCount: Int = 0,
    val inboundCount: Int = 0,
    val customerCount: Int = 0,
    val stockCount: Int = 0,
    val returnCount: Int = 0,
    val paymentCount: Int = 0
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: StockRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        // Here you would ideally have flows in the repository for these counts
        // For now, I'll just collect what we have and update the state
        viewModelScope.launch {
            repository.getAllItems().collectLatest { items ->
                // This is just a placeholder, update counts based on actual data
                _state.value = _state.value.copy(stockCount = items.size)
            }
        }
        
        viewModelScope.launch {
            repository.getAllCustomers().collectLatest { customers ->
                _state.value = _state.value.copy(customerCount = customers.size)
            }
        }
        
        // Mocking other counts for the UI demonstration
        _state.value = _state.value.copy(
            outboundCount = 56,
            inboundCount = 64,
            returnCount = 45,
            paymentCount = 63
        )
    }
}
