package com.example.data.repository

import com.example.data.local.TransactionDao
import com.example.data.local.TransactionEntity
import com.example.model.TransactionRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepository(
    private val transactionDao: TransactionDao
) {
    val allTransactions: Flow<List<TransactionRecord>> =
        transactionDao.getAllTransactions().map { entities ->
            entities.map { it.toTransactionRecord() }
        }

    val totalRevenue: Flow<Int> =
        transactionDao.getTotalRevenue().map { it ?: 0 }

    val qrisRevenue: Flow<Int> =
        transactionDao.getTotalByMethod("QRIS").map { it ?: 0 }

    val cashRevenue: Flow<Int> =
        transactionDao.getTotalByMethod("Tunai").map { it ?: 0 }

    suspend fun insertTransaction(transaction: TransactionRecord) {
        transactionDao.insertTransaction(
            TransactionEntity.fromTransactionRecord(transaction)
        )
    }

    suspend fun deleteTransaction(id: String) {
        transactionDao.deleteTransactionById(id)
    }

    suspend fun clearAll() {
        transactionDao.clearAllTransactions()
    }

    suspend fun prepopulateIfEmpty() {
        if (transactionDao.getTransactionCount() == 0) {
            val now = System.currentTimeMillis()
            val initial = listOf(
                TransactionEntity("TRX-014", "13:40 WIB", 25000, "Nasi Ayam (1), Es Jeruk (2)", "QRIS", now - 60_000),
                TransactionEntity("TRX-013", "13:15 WIB", 36000, "Nasi Campur (2), Kopi (2)", "QRIS", now - 120_000),
                TransactionEntity("TRX-012", "12:50 WIB", 16000, "Bakso Malang (1), Es Teh (1)", "Tunai", now - 180_000),
                TransactionEntity("TRX-011", "12:20 WIB", 35000, "Nasi Telor (3), Gorengan (5)", "QRIS", now - 240_000),
                TransactionEntity("TRX-010", "11:45 WIB", 16000, "Kopi Tubruk (3), Pisang Goreng (4)", "Tunai", now - 300_000),
                TransactionEntity("TRX-009", "11:10 WIB", 26000, "Nasi Telor (2), Es Teh (2)", "QRIS", now - 360_000),
                TransactionEntity("TRX-008", "10:30 WIB", 32000, "Nasi Soto Ayam (2), Gorengan (4)", "QRIS", now - 420_000),
                TransactionEntity("TRX-007", "09:50 WIB", 24000, "Kopi Susu (3), Gorengan (6)", "Tunai", now - 480_000),
                TransactionEntity("TRX-006", "09:15 WIB", 15000, "Nasi Telor (1), Es Jeruk (1)", "QRIS", now - 540_000),
                TransactionEntity("TRX-005", "08:40 WIB", 34000, "Nasi Campur (2), Teh Panas (2)", "QRIS", now - 600_000),
                TransactionEntity("TRX-004", "08:15 WIB", 18000, "Gorengan (10), Kopi Tubruk (2)", "Tunai", now - 660_000),
                TransactionEntity("TRX-003", "07:50 WIB", 24000, "Nasi Uduk Spesial (2)", "QRIS", now - 720_000),
                TransactionEntity("TRX-002", "07:20 WIB", 13000, "Kopi Panas (2), Pisang Goreng (5)", "Tunai", now - 780_000),
                TransactionEntity("TRX-001", "07:00 WIB", 36000, "Sarapan Nasi Telor (3), Es Teh (2)", "QRIS", now - 840_000)
            )
            // Sum of initial: 25k+36k+16k+35k+16k+26k+32k+24k+15k+34k+18k+24k+13k+36k = 350k (matches total in UI)
            transactionDao.insertAll(initial)
        }
    }
}
