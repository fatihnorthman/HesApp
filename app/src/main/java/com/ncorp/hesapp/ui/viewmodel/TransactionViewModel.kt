/*
 * TransactionViewModel.kt
 *
 * Bu dosya, işlemler (gelir, gider, borç, alacak) ekranı için ViewModel sınıfını içerir.
 * ViewModel, MVVM mimarisinde UI ile veri katmanı arasında köprü görevi görür ve ekranın durumunu yönetir.
 *
 * Temel görevleri:
 * - Tüm işlemleri yükler ve saklar
 * - Filtreleme ve arama işlemlerini yönetir
 * - Kullanıcı arayüzüne (UI) güncel ve filtrelenmiş veri sağlar
 * - Repository ile haberleşerek veritabanı işlemlerini gerçekleştirir
 * - Hata ve loading durumlarını yönetir
 *
 * Bu dosyada, her fonksiyonun ve önemli kod bloğunun üstünde detaylı açıklamalar bulacaksınız.
 * Kodun her adımı, "neden böyle yapıldı?" ve "ne işe yarar?" sorularına cevap verecek şekilde açıklanmıştır.
 */
package com.ncorp.hesapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ncorp.hesapp.data.model.Transaction
import com.ncorp.hesapp.data.model.TransactionType
import com.ncorp.hesapp.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Transaction ViewModel
 * 
 * İşlem ekranı için ViewModel sınıfı.
 * 
 * Özellikler:
 * - İşlem listesi yönetimi
 * - Filtreleme ve arama
 * - State management
 * - Repository ile iletişim
 */
@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    // Filtreleme
    private val _selectedFilter = MutableStateFlow<TransactionType?>(null)
    private val _searchQuery = MutableStateFlow("")

    // İşlem listesi
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())

    init {
        viewModelScope.launch {
            transactionRepository.getAllTransactions()
                .catch { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Bir hata oluştu"
                    )
                }
                .collect { transactions ->
                    _transactions.value = transactions
                    _uiState.value = _uiState.value.copy(
                        transactions = transactions,
                        isEmpty = transactions.isEmpty()
                    )
                }
        }
        setupFiltering()
    }

    /**
     * Filtreleme kurulumu (Performance optimized)
     */
    private fun setupFiltering() {
        viewModelScope.launch {
            try {
            combine(
                _transactions,
                _selectedFilter,
                _searchQuery.debounce(300) // Debounce search to reduce filtering frequency
            ) { transactions, filter, query ->
                // Early return for empty list
                if (transactions.isEmpty()) return@combine emptyList<Transaction>()
                
                var filteredTransactions = transactions

                // Apply filters efficiently
                if (query.isNotBlank()) {
                    val lowercaseQuery = query.lowercase()
                    filteredTransactions = filteredTransactions.filter { transaction ->
                        transaction.description.lowercase().contains(lowercaseQuery) ||
                        transaction.category.lowercase().contains(lowercaseQuery)
                    }
                }

                // Type filter
                filter?.let { type ->
                    filteredTransactions = filteredTransactions.filter { transaction ->
                        transaction.type == type
                    }
                }

                filteredTransactions
            }
            .distinctUntilChanged() // Avoid unnecessary updates
            .collect { filteredTransactions ->
                _uiState.value = _uiState.value.copy(
                    transactions = filteredTransactions,
                    isEmpty = filteredTransactions.isEmpty(),
                    error = null
                )
            }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Filtreleme sırasında hata oluştu: ${e.message}"
                )
            }
        }
    }

    /**
     * Filtre değiştir
     */
    fun setFilter(type: TransactionType?) {
        _selectedFilter.value = type
    }

    /**
     * Arama sorgusu değiştir
     */
    fun setSearchQuery(query: String) {
        try {
            _searchQuery.value = query
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                error = "Arama yapılırken hata oluştu: ${e.message}"
            )
        }
    }

    /**
     * İşlem ekle
     */
    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                transactionRepository.insertTransaction(transaction)
                _uiState.value = _uiState.value.copy(
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "İşlem eklenirken hata oluştu: ${e.message}"
                )
            }
        }
    }

    /**
     * İşlem güncelle
     */
    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                transactionRepository.updateTransaction(transaction)
                _uiState.value = _uiState.value.copy(
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "İşlem güncellenirken hata oluştu: ${e.message}"
                )
            }
        }
    }

    /**
     * İşlem sil
     */
    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                transactionRepository.deleteTransaction(transaction)
                _uiState.value = _uiState.value.copy(
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "İşlem silinirken hata oluştu: ${e.message}"
                )
            }
        }
    }

    /**
     * Hata mesajını temizle
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * Verileri yenile
     */
    fun refreshData() {
        viewModelScope.launch {
            transactionRepository.getAllTransactions()
                .catch { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Bir hata oluştu"
                    )
                }
                .collect { transactions ->
                    _transactions.value = transactions
                    _uiState.value = _uiState.value.copy(
                        transactions = transactions,
                        isEmpty = transactions.isEmpty()
                    )
                }
        }
    }

    /**
     * Örnek veriler ekle (test için)
     */
    fun insertSampleData() {
        viewModelScope.launch {
            try {
                transactionRepository.insertSampleData()
                _uiState.value = _uiState.value.copy(
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Örnek veriler eklenirken hata oluştu: ${e.message}"
                )
            }
        }
    }
}

/**
 * Transaction UI State
 * 
 * İşlem ekranının durumunu temsil eder.
 */
data class TransactionUiState(
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = false,
    val isEmpty: Boolean = false,
    val error: String? = null
) 