package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.TransactionRecord

/**
 * Entity representing stored sales / transaction records in the Room database.
 * Includes fields for:
 * - id: Unique transaction identifier
 * - timestamp: Formatted date/time string of the sale
 * - total: Total sale amount in Rupiah
 * - itemsSummary: Serialized summary of purchased items (e.g. "Nasi Telor (2), Es Jeruk (1)")
 * - method: Payment method (e.g. "QRIS", "Tunai")
 * - createdAt: Timestamp in milliseconds for chronological sorting
 */
@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    val id: String,
    val timestamp: String,
    val total: Int,
    val itemsSummary: String,
    val method: String = "QRIS", // "QRIS" or "Tunai"
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toTransactionRecord(): TransactionRecord {
        return TransactionRecord(
            id = id,
            timestamp = timestamp,
            itemsSummary = itemsSummary,
            total = total,
            method = method
        )
    }

    companion object {
        fun fromTransactionRecord(
            record: TransactionRecord,
            createdAt: Long = System.currentTimeMillis()
        ): TransactionEntity {
            return TransactionEntity(
                id = record.id,
                timestamp = record.timestamp,
                total = record.total,
                itemsSummary = record.itemsSummary,
                method = record.method,
                createdAt = createdAt
            )
        }
    }
}
