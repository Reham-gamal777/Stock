package com.example.stock.data.local.dao

import androidx.room.*
import com.example.stock.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StockDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ItemEntity)

    @Query("SELECT * FROM items")
    fun getAllItems(): Flow<List<ItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: CustomerEntity)

    @Query("SELECT * FROM customers")
    fun getAllCustomers(): Flow<List<CustomerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStock(stock: StockEntity)

    @Query("SELECT * FROM stock")
    fun getAllStock(): Flow<List<StockEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutbound(outbound: OutboundEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutboundDetails(details: List<OutboundDetailsEntity>)

    @Transaction
    @Query("SELECT * FROM outbounds")
    fun getAllOutbounds(): Flow<List<OutboundEntity>>

    @Query("SELECT * FROM outbounds WHERE customerId = :customerId")
    suspend fun getOutboundsForCustomer(customerId: Int): List<OutboundEntity>

    @Query("SELECT * FROM outbound_details WHERE outboundId = :outboundId")
    suspend fun getDetailsForOutbound(outboundId: Long): List<OutboundDetailsEntity>

    @Query("SELECT * FROM customers WHERE id = :customerId")
    suspend fun getCustomerById(customerId: Int): CustomerEntity?

    @Query("SELECT * FROM items WHERE id = :itemId")
    suspend fun getItemById(itemId: Int): ItemEntity?

    // Inbound
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInbound(inbound: InboundEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInboundDetails(details: List<InboundDetailsEntity>)

    @Query("SELECT * FROM inbounds")
    fun getAllInbounds(): Flow<List<InboundEntity>>

    @Query("SELECT * FROM inbound_details WHERE inboundId = :inboundId")
    suspend fun getDetailsForInbound(inboundId: Int): List<InboundDetailsEntity>

    // Movement Tracking
    @Query("SELECT * FROM inbound_details WHERE itemId = :itemId")
    suspend fun getInboundDetailsByItem(itemId: Int): List<InboundDetailsEntity>

    @Query("SELECT * FROM outbound_details WHERE itemId = :itemId")
    suspend fun getOutboundDetailsByItem(itemId: Int): List<OutboundDetailsEntity>

    @Query("SELECT * FROM inbounds WHERE id = :inboundId")
    suspend fun getInboundById(inboundId: Int): InboundEntity?

    @Query("SELECT * FROM outbounds WHERE id = :outboundId")
    suspend fun getOutboundById(outboundId: Long): OutboundEntity?

    // Returned
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReturned(returned: ReturnedEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReturnedDetails(details: List<ReturnedDetailsEntity>)

    @Query("SELECT * FROM returned")
    fun getAllReturned(): Flow<List<ReturnedEntity>>

    @Query("SELECT * FROM returned_details WHERE returnedId = :returnedId")
    suspend fun getDetailsForReturned(returnedId: Int): List<ReturnedDetailsEntity>
    
    @Query("SELECT * FROM returned_details WHERE itemId = :itemId")
    suspend fun getReturnedDetailsByItem(itemId: Int): List<ReturnedDetailsEntity>

    // Payments
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity)

    @Query("SELECT * FROM payments ORDER BY date DESC")
    fun getAllPayments(): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE customerId = :customerId ORDER BY date DESC")
    suspend fun getPaymentsForCustomer(customerId: Int): List<PaymentEntity>
}
