package com.ncorp.hesapp.data.dao

import androidx.room.*
import com.ncorp.hesapp.data.model.Transaction
import com.ncorp.hesapp.data.model.TransactionType
import kotlinx.coroutines.flow.Flow

/*
 * TransactionDao.kt
 *
 * Bu dosya, işlemler tablosuna (transactions) erişim sağlayan Room DAO (Data Access Object) arayüzünü içerir.
 * DAO, veritabanı işlemlerini (ekle, sil, güncelle, sorgula) tanımlar ve uygular.
 *
 * Temel görevleri:
 * - SQL sorguları ile veritabanı işlemlerini yönetmek
 * - Flow ile reaktif veri akışı sağlamak
 * - Performanslı ve güvenli veri erişimi sunmak
 * - CRUD işlemlerini (Create, Read, Update, Delete) kolaylaştırmak
 *
 * Kodun her adımı, "neden böyle yapıldı?" ve "ne işe yarar?" sorularına cevap verecek şekilde açıklanmıştır.
 */
@Dao
interface TransactionDao {

    /**
     * Tüm işlemleri getir (with LIMIT for initial loading)
     */
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Transaction>>
    
    /**
     * Sayfalı işlemler getir (pagination için)
     */
    @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT :limit OFFSET :offset")
    suspend fun getTransactionsPaged(limit: Int, offset: Int): List<Transaction>

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
     * Para birimine göre toplam gelir hesapla
     */
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'INCOME' AND currency = :currency")
    fun getTotalIncomeByCurrency(currency: String): Flow<Double?>

    /**
     * Para birimine göre toplam gider hesapla
     */
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'EXPENSE' AND currency = :currency")
    fun getTotalExpenseByCurrency(currency: String): Flow<Double?>

    /**
     * Para birimine göre toplam borç hesapla
     */
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'DEBT' AND currency = :currency")
    fun getTotalDebtByCurrency(currency: String): Flow<Double?>

    /**
     * Para birimine göre toplam alacak hesapla
     */
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'RECEIVABLE' AND currency = :currency")
    fun getTotalReceivableByCurrency(currency: String): Flow<Double?>

    /**
     * Para birimine göre net durum hesapla
     */
    @Query("SELECT (SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'INCOME' AND currency = :currency) - (SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'EXPENSE' AND currency = :currency)")
    fun getNetAmountByCurrency(currency: String): Flow<Double?>

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
    
    /**
     * Toplu işlem ekleme (batch insert for better performance)
     */
    @Insert
    suspend fun insertTransactions(transactions: List<Transaction>): List<Long>
    
    /**
     * En son N işlemi getir (dashboard için optimize edilmiş)
     */
    @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT :limit")
    fun getLatestTransactions(limit: Int): Flow<List<Transaction>>

    /**
     * Belirli bir borç işlemine bağlı tüm borç ödemelerinin toplamını getir
     */
    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE parentTransactionId = :debtId AND type = 'DEBT_PAYMENT'")
    suspend fun getTotalPaymentsForDebt(debtId: Long): Double

    /**
     * Belirli bir alacak işlemine bağlı tüm tahsilatların toplamını getir
     */
    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE parentTransactionId = :receivableId AND type = 'RECEIVABLE_COLLECTION'")
    suspend fun getTotalCollectionsForReceivable(receivableId: Long): Double
    
    /**
     * Performanslı istatistik sorgusu (tek sorguda tüm toplamlar)
     */
    @Query("""
        SELECT 
            COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amount ELSE 0 END), 0) as totalIncome,
            COALESCE(SUM(CASE WHEN type = 'EXPENSE' THEN amount ELSE 0 END), 0) as totalExpense,
            COALESCE(SUM(CASE WHEN type = 'DEBT' THEN amount ELSE 0 END), 0) as totalDebt,
            COALESCE(SUM(CASE WHEN type = 'RECEIVABLE' THEN amount ELSE 0 END), 0) as totalReceivable,
            COUNT(*) as totalCount
        FROM transactions
    """)
    fun getFinancialSummary(): Flow<FinancialSummary>
}

/**
 * Kategori özeti data class'ı
 */
data class CategorySummary(
    val category: String,
    val total: Double
)

/**
 * Finansal özet data class'ı (tek sorgu performansı için)
 */
data class FinancialSummary(
    val totalIncome: Double,
    val totalExpense: Double,
    val totalDebt: Double,
    val totalReceivable: Double,
    val totalCount: Int
) {
    val netAmount: Double get() = totalIncome - totalExpense
    val balance: Double get() = totalIncome - totalExpense + totalReceivable - totalDebt
} 