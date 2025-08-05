package com.ncorp.hesapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ncorp.hesapp.data.model.Transaction
import com.ncorp.hesapp.data.model.TransactionType
import com.ncorp.hesapp.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<AddTransactionUiState>(AddTransactionUiState.Idle)
    val uiState: LiveData<AddTransactionUiState> = _uiState

    fun addTransaction(
        type: TransactionType,
        description: String,
        category: String,
        amount: Double,
        date: Date,
        notes: String? = null,
        contactId: Long? = null
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = AddTransactionUiState.Loading

                val transaction = Transaction(
                    type = type,
                    description = description,
                    category = category,
                    amount = amount,
                    date = date,
                    notes = notes,
                    contactId = contactId,
                    createdAt = Date(),
                    updatedAt = Date()
                )

                transactionRepository.addTransaction(transaction)
                _uiState.value = AddTransactionUiState.Success

            } catch (e: Exception) {
                _uiState.value = AddTransactionUiState.Error(
                    message = e.message ?: "İşlem kaydedilirken bir hata oluştu"
                )
            }
        }
    }

    sealed class AddTransactionUiState {
        object Idle : AddTransactionUiState()
        object Loading : AddTransactionUiState()
        object Success : AddTransactionUiState()
        data class Error(val message: String) : AddTransactionUiState()
    }
} 