package com.example.stock.presentation.home

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
    private val itemRepository: ItemRepository,
    private val customerRepository: CustomerRepository,
    private val inboundRepository: InboundRepository,
    private val outboundRepository: OutboundRepository,
    private val returnedRepository: ReturnedRepository,
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        observeAllCounts()
    }

    private fun observeAllCounts() {
        viewModelScope.launch {
            itemRepository.getAllItems().collectLatest { 
                _state.value = _state.value.copy(stockCount = it.size) 
            }
        }
        viewModelScope.launch {
            customerRepository.getAllCustomers().collectLatest { 
                _state.value = _state.value.copy(customerCount = it.size) 
            }
        }
        viewModelScope.launch {
            outboundRepository.getAllOutbounds().collectLatest { 
                _state.value = _state.value.copy(outboundCount = it.size) 
            }
        }
        viewModelScope.launch {
            inboundRepository.getAllInbounds().collectLatest { 
                _state.value = _state.value.copy(inboundCount = it.size) 
            }
        }
        viewModelScope.launch {
            returnedRepository.getAllReturned().collectLatest { 
                _state.value = _state.value.copy(returnCount = it.size) 
            }
        }
        viewModelScope.launch {
            paymentRepository.getAllPayments().collectLatest { 
                _state.value = _state.value.copy(paymentCount = it.size)
            }
        }
    }
}
