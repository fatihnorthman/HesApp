/*
 * TransactionRepository.kt
 *
 * Bu dosya, işlemlerle ilgili veri yönetimini sağlayan Repository sınıfını içerir.
 * Repository, MVVM mimarisinde veri kaynakları (veritabanı, ağ, cache) ile ViewModel arasında köprü görevi görür.
 *
 * Temel görevleri:
 * - DAO üzerinden veritabanı işlemlerini yönetmek
 * - Verileri cache'lemek ve performansı artırmak
 * - ViewModel'e reaktif (Flow) veri sağlamak
 * - Dependency injection ile test edilebilir ve modüler yapı sunmak
 *
 * Kodun her adımı, "neden böyle yapıldı?" ve "ne işe yarar?" sorularına cevap verecek şekilde açıklanmıştır.
 */
package com.ncorp.hesapp.data.repository

import com.ncorp.hesapp.data.dao.TransactionDao
import com.ncorp.hesapp.data.dao.FinancialSummary
import com.ncorp.hesapp.data.model.Transaction
import com.ncorp.hesapp.data.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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
    // Shared scope for caching flows
    private val repositoryScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Cached flows for better performance
    private val _allTransactions = transactionDao.getAllTransactions()
        .shareIn(repositoryScope, kotlinx.coroutines.flow.SharingStarted.Eagerly, 1)
    
    private val _financialSummary = transactionDao.getFinancialSummary()
        .shareIn(repositoryScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), 1)

    /**
     * Tüm işlemleri getir (cached)
     */
    fun getAllTransactions(): Flow<List<Transaction>> {
        return _allTransactions
    }

    /**
     * Finansal özet getir (optimized single query)
     */
    fun getFinancialSummary(): Flow<FinancialSummary> {
        return _financialSummary
    }

    /**
     * Sayfalı işlemler getir (pagination için)
     */
    suspend fun getTransactionsPaged(limit: Int, offset: Int): List<Transaction> {
        return transactionDao.getTransactionsPaged(limit, offset)
    }

    /**
     * En son N işlemi getir (dashboard için optimize edilmiş)
     */
    fun getLatestTransactions(limit: Int = 10): Flow<List<Transaction>> {
        return transactionDao.getLatestTransactions(limit)
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
        return try {
            val transactionId = transactionDao.insertTransaction(transaction)
            if (transactionId <= 0) {
                throw Exception("İşlem kaydedilemedi")
            }
            transactionId
        } catch (e: Exception) {
            throw Exception("İşlem eklenirken hata oluştu: ${e.message}", e)
        }
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
        try {
            transactionDao.updateTransaction(transaction)
        } catch (e: Exception) {
            throw Exception("İşlem güncellenirken hata oluştu: ${e.message}", e)
        }
    }

    /**
     * İşlem sil
     */
    suspend fun deleteTransaction(transaction: Transaction) {
        try {
            transactionDao.deleteTransaction(transaction)
        } catch (e: Exception) {
            throw Exception("İşlem silinirken hata oluştu: ${e.message}", e)
        }
    }

    /**
     * ID'ye göre işlem getir
     */
    suspend fun getTransactionById(id: Long): Transaction? {
        return try {
            transactionDao.getTransactionById(id)
        } catch (e: Exception) {
            throw Exception("İşlem getirilirken hata oluştu: ${e.message}", e)
        }
    }

    suspend fun getTotalPaymentsForDebt(debtId: Long): Double {
        return try {
            transactionDao.getTotalPaymentsForDebt(debtId)
        } catch (e: Exception) {
            throw Exception("Borç ödemeleri alınırken hata oluştu: ${e.message}", e)
        }
    }

    suspend fun getTotalCollectionsForReceivable(receivableId: Long): Double {
        return try {
            transactionDao.getTotalCollectionsForReceivable(receivableId)
        } catch (e: Exception) {
            throw Exception("Alacak tahsilatları alınırken hata oluştu: ${e.message}", e)
        }
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
     * Toplu işlem ekleme (batch insert for better performance)
     */
    suspend fun insertTransactions(transactions: List<Transaction>): List<Long> {
        return try {
            if (transactions.isEmpty()) {
                throw Exception("Eklenecek işlem listesi boş")
            }
            transactionDao.insertTransactions(transactions)
        } catch (e: Exception) {
            throw Exception("Toplu işlem eklenirken hata oluştu: ${e.message}", e)
        }
    }

    /**
     * Örnek veriler ekle (test için) - optimized batch insert
     */
    suspend fun insertSampleData() {
        try {
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

            // Use batch insert for better performance
            transactionDao.insertTransactions(sampleTransactions)
        } catch (e: Exception) {
            throw Exception("Örnek veriler eklenirken hata oluştu: ${e.message}", e)
        }
    }
} 