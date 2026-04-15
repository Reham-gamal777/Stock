package com.example.stock.Domain.repository

import com.example.stock.Domain.model.Returned
import com.example.stock.Domain.model.ReturnedDetails
import kotlinx.coroutines.flow.Flow

interface ReturnedRepository {
    fun getAllReturned(): Flow<List<Returned>>
    suspend fun getReturnedDetails(returnedId: Int): List<ReturnedDetails>
    suspend fun getItemReturnedDetails(itemId: Int): List<ReturnedDetails>
    suspend fun insertReturned(returned: Returned, details: List<ReturnedDetails>)
}
