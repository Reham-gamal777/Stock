package com.example.stock.data.repository

import com.example.stock.Domain.model.Returned
import com.example.stock.Domain.model.ReturnedDetails
import com.example.stock.Domain.repository.ReturnedRepository
import com.example.stock.data.local.dao.ReturnedDao
import com.example.stock.data.mapper.toReturned
import com.example.stock.data.mapper.toReturnedDetails
import com.example.stock.data.mapper.toReturnedDetailsEntity
import com.example.stock.data.mapper.toReturnedEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ReturnedRepositoryImpl @Inject constructor(
    private val dao: ReturnedDao
) : ReturnedRepository {
    override fun getAllReturned(): Flow<List<Returned>> = dao.getAllReturned().map { it.map { e -> e.toReturned() } }
    override suspend fun getReturnedDetails(returnedId: Int): List<ReturnedDetails> = dao.getDetailsForReturned(returnedId).map { it.toReturnedDetails() }
    override suspend fun getItemReturnedDetails(itemId: Int): List<ReturnedDetails> = dao.getReturnedDetailsByItem(itemId).map { it.toReturnedDetails() }
    override suspend fun insertReturned(returned: Returned, details: List<ReturnedDetails>) {
        val id = dao.insertReturned(returned.toReturnedEntity())
        dao.insertReturnedDetails(details.map { it.copy(returnedId = id.toInt()).toReturnedDetailsEntity() })
    }
}
