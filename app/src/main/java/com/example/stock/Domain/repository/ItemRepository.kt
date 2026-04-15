package com.example.stock.Domain.repository

import com.example.stock.Domain.model.Item
import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    fun getAllItems(): Flow<List<Item>>
    suspend fun insertItem(item: Item)
    suspend fun getItemById(id: Int): Item?
}
