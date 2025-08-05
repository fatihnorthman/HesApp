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
        loadTransactions()
        setupFiltering()
    }

    /**
     * İşlemleri yükle
     */
    private fun loadTransactions() {
        viewModelScope.launch {
            transactionRepository.getAllTransactions()
                .onStart { _uiState.value = _uiState.value.copy(isLoading = true) }
                .onCompletion { _uiState.value = _uiState.value.copy(isLoading = false) }
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
     * Filtreleme kurulumu
     */
    private fun setupFiltering() {
        viewModelScope.launch {
            combine(
                _transactions,
                _selectedFilter,
                _searchQuery
            ) { transactions, filter, query ->
                var filteredTransactions = transactions

                // Arama filtresi
                if (query.isNotEmpty()) {
                    filteredTransactions = filteredTransactions.filter { transaction ->
                        transaction.description.contains(query, ignoreCase = true) ||
                        transaction.category.contains(query, ignoreCase = true)
                    }
                }

                // Tür filtresi
                filter?.let { type ->
                    filteredTransactions = filteredTransactions.filter { transaction ->
                        transaction.type == type
                    }
                }

                filteredTransactions
            }.collect { filteredTransactions ->
                _uiState.value = _uiState.value.copy(
                    transactions = filteredTransactions,
                    isEmpty = filteredTransactions.isEmpty()
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
        _searchQuery.value = query
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
        loadTransactions()
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