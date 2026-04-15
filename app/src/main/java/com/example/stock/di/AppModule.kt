package com.example.stock.di

import android.content.Context
import androidx.room.Room
import com.example.stock.Domain.repository.*
import com.example.stock.data.local.StockDatabase
import com.example.stock.data.local.dao.*
import com.example.stock.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideStockDatabase(@ApplicationContext context: Context): StockDatabase {
        return Room.databaseBuilder(
            context,
            StockDatabase::class.java,
            "stock_db"
        ).fallbackToDestructiveMigration()
         .build()
    }

    @Provides
    @Singleton
    fun provideItemDao(db: StockDatabase): ItemDao = db.itemDao

    @Provides
    @Singleton
    fun provideCustomerDao(db: StockDatabase): CustomerDao = db.customerDao

    @Provides
    @Singleton
    fun provideStockDao(db: StockDatabase): StockDao = db.stockDao

    @Provides
    @Singleton
    fun provideInboundDao(db: StockDatabase): InboundDao = db.inboundDao

    @Provides
    @Singleton
    fun provideOutboundDao(db: StockDatabase): OutboundDao = db.outboundDao

    @Provides
    @Singleton
    fun provideReturnedDao(db: StockDatabase): ReturnedDao = db.returnedDao

    @Provides
    @Singleton
    fun providePaymentDao(db: StockDatabase): PaymentDao = db.paymentDao

    @Provides
    @Singleton
    fun provideItemRepository(dao: ItemDao): ItemRepository = ItemRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideCustomerRepository(dao: CustomerDao): CustomerRepository = CustomerRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideStockRepository(
        stockDao: StockDao,
        inboundDao: InboundDao,
        outboundDao: OutboundDao,
        returnedDao: ReturnedDao
    ): StockRepository = StockRepositoryImpl(stockDao, inboundDao, outboundDao, returnedDao)

    @Provides
    @Singleton
    fun provideInboundRepository(dao: InboundDao): InboundRepository = InboundRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideOutboundRepository(dao: OutboundDao): OutboundRepository = OutboundRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideReturnedRepository(dao: ReturnedDao): ReturnedRepository = ReturnedRepositoryImpl(dao)

    @Provides
    @Singleton
    fun providePaymentRepository(dao: PaymentDao): PaymentRepository = PaymentRepositoryImpl(dao)
}
