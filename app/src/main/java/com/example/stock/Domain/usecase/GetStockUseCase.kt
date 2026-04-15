package com.example.stock.Domain.usecase

import com.example.stock.Domain.model.Item
import com.example.stock.Domain.repository.ItemRepository
import com.example.stock.Domain.repository.StockRepository
import com.example.stock.presentation.stock.StockItemUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetStockUseCase @Inject constructor(
    private val itemRepository: ItemRepository,
    private val stockRepository: StockRepository
) {
    operator fun invoke(): Flow<List<StockItemUiModel>> {
        return itemRepository.getAllItems().map { items ->
            items.map { item ->
                val inTotal = stockRepository.getItemInboundDetails(item.id).sumOf { it.amount }
                val outTotal = stockRepository.getItemOutboundDetails(item.id).sumOf { it.amount }
                val returnedTotal = stockRepository.getItemReturnedDetails(item.id).sumOf { it.amount }
                
                StockItemUiModel(
                    id = item.id,
                    itemName = item.itemName,
                    balance = inTotal - outTotal + returnedTotal
                )
            }
        }
    }
}
