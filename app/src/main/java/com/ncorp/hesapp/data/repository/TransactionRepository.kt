package com.ncorp.hesapp.data.repository

import com.ncorp.hesapp.data.dao.TransactionDao
import com.ncorp.hesapp.data.model.Transaction
import com.ncorp.hesapp.data.model.TransactionType
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Transaction Repository
 * 
 * İşlem verilerini yöneten repository sınıfı.
 * DAO ile ViewModel arasında köprü görevi görür.
 * 
 * Özellikler:
 * - Veri erişim katmanı
 * - İş mantığı yönetimi
 * - Dependency injection desteği
 * - Singleton pattern
 */
@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao
) {

    /**
     * Tüm işlemleri getir
     */
    fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions()
    }

    /**
     * İşlem türüne göre filtrele
     */
    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByType(type)
    }

    /**
     * Arama yap
     */
    fun searchTransactions(query: String): Flow<List<Transaction>> {
        return transactionDao.searchTransactions(query)
    }

    /**
     * Tarih aralığına göre filtrele
     */
    fun getTransactionsByDateRange(startDate: Date, endDate: Date): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByDateRange(startDate, endDate)
    }

    /**
     * Kişiye göre filtrele
     */
    fun getTransactionsByContact(contactId: Long): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByContact(contactId)
    }

    /**
     * İşlem ekle
     */
    suspend fun insertTransaction(transaction: Transaction): Long {
        return transactionDao.insertTransaction(transaction)
    }

    /**
     * İşlem ekle (addTransaction alias)
     */
    suspend fun addTransaction(transaction: Transaction): Long {
        return insertTransaction(transaction)
    }

    /**
     * İşlem güncelle
     */
    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction)
    }

    /**
     * İşlem sil
     */
    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction)
    }

    /**
     * ID'ye göre işlem getir
     */
    suspend fun getTransactionById(id: Long): Transaction? {
        return transactionDao.getTransactionById(id)
    }

    /**
     * Toplam gelir
     */
    fun getTotalIncome(): Flow<Double?> {
        return transactionDao.getTotalIncome()
    }

    /**
     * Toplam gider
     */
    fun getTotalExpense(): Flow<Double?> {
        return transactionDao.getTotalExpense()
    }

    /**
     * Toplam borç
     */
    fun getTotalDebt(): Flow<Double?> {
        return transactionDao.getTotalDebt()
    }

    /**
     * Toplam alacak
     */
    fun getTotalReceivable(): Flow<Double?> {
        return transactionDao.getTotalReceivable()
    }

    /**
     * Net durum
     */
    fun getNetAmount(): Flow<Double?> {
        return transactionDao.getNetAmount()
    }

    /**
     * Kategoriye göre grupla
     */
    fun getTransactionsByCategory(): Flow<List<com.ncorp.hesapp.data.dao.CategorySummary>> {
        return transactionDao.getTransactionsByCategory()
    }

    /**
     * Son işlemler
     */
    fun getRecentTransactions(): Flow<List<Transaction>> {
        return transactionDao.getRecentTransactions()
    }

    /**
     * İşlem sayısı
     */
    fun getTransactionCount(): Flow<Int> {
        return transactionDao.getTransactionCount()
    }

    /**
     * Örnek veriler ekle (test için)
     */
    suspend fun insertSampleData() {
        val sampleTransactions = listOf(
            Transaction(
                type = TransactionType.INCOME,
                description = "Müşteri ödemesi",
                category = "Satış",
                amount = 1250.0,
                date = Date()
            ),
            Transaction(
                type = TransactionType.EXPENSE,
                description = "Ofis malzemeleri",
                category = "Gider",
                amount = 150.0,
                date = Date()
            ),
            Transaction(
                type = TransactionType.DEBT,
                description = "Tedarikçi borcu",
                category = "Borç",
                amount = 500.0,
                date = Date()
            ),
            Transaction(
                type = TransactionType.RECEIVABLE,
                description = "Müşteri alacağı",
                category = "Alacak",
                amount = 800.0,
                date = Date()
            )
        )

        sampleTransactions.forEach { transaction ->
            transactionDao.insertTransaction(transaction)
        }
    }
} 