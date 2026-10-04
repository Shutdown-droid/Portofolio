package com.example.model

data class OrderItem(
    val id: Long,
    val name: String,
    val qty: Int,
    val unitPrice: Int,
    val total: Int = qty * unitPrice
)

data class TransactionRecord(
    val id: String,
    val timestamp: String,
    val itemsSummary: String,
    val total: Int,
    val method: String // "QRIS" or "Tunai"
)

data class PopularMenuItem(
    val name: String,
    val soldCount: Int,
    val unitLabel: String,
    val percentage: Float
)
