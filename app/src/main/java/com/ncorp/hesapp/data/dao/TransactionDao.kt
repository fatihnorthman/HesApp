package com.ncorp.hesapp.data.dao

import androidx.room.*
import com.ncorp.hesapp.data.model.Transaction
import com.ncorp.hesapp.data.model.TransactionType
import kotlinx.coroutines.flow.Flow

/**
 * Transaction DAO (Data Access Object)
 * 
 * İşlem verilerine erişim için Room DAO interface'i.
 * Veritabanı işlemlerini yönetir.
 * 
 * Özellikler:
 * - CRUD işlemleri (Create, Read, Update, Delete)
 * - Filtreleme ve arama
 * - Flow ile reaktif veri akışı
 * - İstatistik sorguları
 */
@Dao
interface TransactionDao {

    /**
     * Tüm işlemleri getir
     */
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    /**
     * İşlem türüne göre filtrele
     */
    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC")
    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>>

    /**
     * Arama sorgusu
     */
    @Query("SELECT * FROM transactions WHERE description LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' ORDER BY date DESC")
    fun searchTransactions(query: String): Flow<List<Transaction>>

    /**
     * Tarih aralığına göre filtrele
     */
    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getTransactionsByDateRange(startDate: java.util.Date, endDate: java.util.Date): Flow<List<Transaction>>

    /**
     * Kişiye göre filtrele
     */
    @Query("SELECT * FROM transactions WHERE contactId = :contactId ORDER BY date DESC")
    fun getTransactionsByContact(contactId: Long): Flow<List<Transaction>>

    /**
     * İşlem ekle
     */
    @Insert
    suspend fun insertTransaction(transaction: Transaction): Long

    /**
     * İşlem güncelle
     */
    @Update
    suspend fun updateTransaction(transaction: Transaction)

    /**
     * İşlem sil
     */
    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    /**
     * ID'ye göre işlem getir
     */
    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): Transaction?

    /**
     * Toplam gelir hesapla
     */
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'INCOME'")
    fun getTotalIncome(): Flow<Double?>

    /**
     * Toplam gider hesapla
     */
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'EXPENSE'")
    fun getTotalExpense(): Flow<Double?>

    /**
     * Toplam borç hesapla
     */
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'DEBT'")
    fun getTotalDebt(): Flow<Double?>

    /**
     * Toplam alacak hesapla
     */
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'RECEIVABLE'")
    fun getTotalReceivable(): Flow<Double?>

    /**
     * Net durum hesapla (Gelir - Gider)
     */
    @Query("SELECT (SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'INCOME') - (SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'EXPENSE')")
    fun getNetAmount(): Flow<Double?>

    /**
     * Kategoriye göre grupla
     */
    @Query("SELECT category, SUM(amount) as total FROM transactions GROUP BY category ORDER BY total DESC")
    fun getTransactionsByCategory(): Flow<List<CategorySummary>>

    /**
     * Son 30 günlük işlemler
     */
    @Query("SELECT * FROM transactions WHERE date >= datetime('now', '-30 days') ORDER BY date DESC")
    fun getRecentTransactions(): Flow<List<Transaction>>

    /**
     * İşlem sayısını getir
     */
    @Query("SELECT COUNT(*) FROM transactions")
    fun getTransactionCount(): Flow<Int>
}

/**
 * Kategori özeti data class'ı
 */
data class CategorySummary(
    val category: String,
    val total: Double
) 