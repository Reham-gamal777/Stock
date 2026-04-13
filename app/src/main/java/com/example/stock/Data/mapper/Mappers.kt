package com.example.stock.data.mapper

import com.example.stock.Domain.model.*
import com.example.stock.data.local.entities.*

fun ItemEntity.toItem(): Item = Item(id, itemName, itemNum)
fun Item.toItemEntity(): ItemEntity = ItemEntity(id, itemName, itemNum)

fun CustomerEntity.toCustomer(): Customer = Customer(id, userId, customerName, customerNum, customerDebt, isSynced)
fun Customer.toCustomerEntity(): CustomerEntity = CustomerEntity(id, userId, customerName, customerNum, customerDebt, isSynced)

fun StockEntity.toStock(): Stock = Stock(id, itemId, userId, initAmount, currentAmount, firstDate, isSynced)
fun Stock.toStockEntity(): StockEntity = StockEntity(id, itemId, userId, initAmount, currentAmount, firstDate, isSynced)

fun OutboundEntity.toOutbound(): Outbound = Outbound(id, userId, customerId, invoiceNumber, image, outboundDate, latitude, longitude, moneyReceived, isSynced)
fun Outbound.toOutboundEntity(): OutboundEntity = OutboundEntity(id, userId, customerId, invoiceNumber, image, outboundDate, latitude, longitude, moneyReceived, isSynced)

fun OutboundDetailsEntity.toOutboundDetails(): OutboundDetails = OutboundDetails(id, outboundId, itemId, amount, price, isSynced)
fun OutboundDetails.toOutboundDetailsEntity(): OutboundDetailsEntity = OutboundDetailsEntity(id, outboundId, itemId, amount, price, isSynced)

fun InboundEntity.toInbound(): Inbound = Inbound(id, invoiceNumber, supplierName, date, totalAmount, status, description, isSynced)
fun Inbound.toInboundEntity(): InboundEntity = InboundEntity(id, invoiceNumber, supplierName, date, totalAmount, status, description, isSynced)

fun InboundDetailsEntity.toInboundDetails(): InboundDetails = InboundDetails(id, inboundId.toInt(), itemId, amount)
fun InboundDetails.toInboundDetailsEntity(): InboundDetailsEntity = InboundDetailsEntity(id, inboundId, itemId, amount)

fun ReturnedEntity.toReturned(): Returned = Returned(id, customerId, returnedDate, userId, latitude, longitude, isSynced)
fun Returned.toReturnedEntity(): ReturnedEntity = ReturnedEntity(id, customerId, returnedDate, userId, latitude, longitude, isSynced)

fun ReturnedDetailsEntity.toReturnedDetails(): ReturnedDetails = ReturnedDetails(id, returnedId, itemId, amount, price)
fun ReturnedDetails.toReturnedDetailsEntity(): ReturnedDetailsEntity = ReturnedDetailsEntity(id, returnedId, itemId, amount, price)

fun PaymentEntity.toPayment(): Payment = Payment(id, customerId, amount, paymentType, date)
fun Payment.toPaymentEntity(): PaymentEntity = PaymentEntity(id, customerId, amount, paymentType, date)
