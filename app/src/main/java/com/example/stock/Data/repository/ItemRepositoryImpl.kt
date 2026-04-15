package com.example.stock.data.repository

import com.example.stock.Domain.model.Item
import com.example.stock.Domain.repository.ItemRepository
import com.example.stock.data.local.dao.ItemDao
import com.example.stock.data.mapper.toItem
import com.example.stock.data.mapper.toItemEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ItemRepositoryImpl @Inject constructor(
    private val dao: ItemDao
) : ItemRepository {
    override fun getAllItems(): Flow<List<Item>> = dao.getAllItems().map { it.map { e -> e.toItem() } }
    override suspend fun insertItem(item: Item) = dao.insertItem(item.toItemEntity())
    override suspend fun getItemById(id: Int): Item? = dao.getItemById(id)?.toItem()
}
