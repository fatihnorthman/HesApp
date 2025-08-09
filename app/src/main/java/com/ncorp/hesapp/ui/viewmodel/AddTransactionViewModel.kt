/*
 * AddTransactionViewModel.kt
 *
 * Bu dosya, yeni bir finansal işlem ekleme ekranı için ViewModel sınıfını içerir.
 * ViewModel, MVVM mimarisinde UI ile veri katmanı arasında köprü görevi görür ve ekranın durumunu yönetir.
 *
 * Temel görevleri:
 * - Kullanıcıdan alınan verilerle yeni bir Transaction (işlem) nesnesi oluşturmak
 * - Repository üzerinden veritabanına kaydetmek
 * - Ekranın durumunu (başarılı, hata, yükleniyor) LiveData ile UI'a bildirmek
 * - Hata yönetimi ve coroutine kullanımı
 *
 * Kodun her adımı, "neden böyle yapıldı?" ve "ne işe yarar?" sorularına cevap verecek şekilde açıklanmıştır.
 */
package com.ncorp.hesapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ncorp.hesapp.data.model.Transaction
import com.ncorp.hesapp.data.model.TransactionType
import com.ncorp.hesapp.data.model.Currency
import com.ncorp.hesapp.data.repository.ProductRepository
import com.ncorp.hesapp.data.repository.BankAccountRepository
import com.ncorp.hesapp.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val productRepository: ProductRepository,
    private val bankAccountRepository: BankAccountRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<AddTransactionUiState>(AddTransactionUiState.Idle)
    val uiState: LiveData<AddTransactionUiState> = _uiState

    fun addTransaction(
        type: TransactionType,
        description: String,
        category: String,
        amount: Double,
        currency: Currency,
        date: Date,
        notes: String?,
        contactId: Long? = null,
        productId: Long? = null,
        quantity: Int? = null,
        accountId: Long? = null,
        isSaleFlow: Boolean = false,
        isPurchaseFlow: Boolean = false
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = AddTransactionUiState.Loading
                
                // Ürün stok güncellemesi (satış/alış)
                if (productId != null && quantity != null && quantity > 0) {
                    if (isSaleFlow) {
                        val updated = productRepository.decrementStock(productId, quantity)
                        if (updated == 0) {
                            _uiState.value = AddTransactionUiState.Error("Yetersiz stok")
                            return@launch
                        }
                    } else if (isPurchaseFlow) {
                        val updated = productRepository.incrementStock(productId, quantity)
                        if (updated == 0) {
                            _uiState.value = AddTransactionUiState.Error("Stok güncellenemedi")
                            return@launch
                        }
                    }
                }

                val transaction = Transaction(
                    type = type,
                    description = description,
                    category = category,
                    amount = amount,
                    currency = currency,
                    date = date,
                    notes = notes,
                    contactId = contactId,
                    accountId = accountId,
                    productId = productId
                )
                
                val transactionId = transactionRepository.addTransaction(transaction)
                
                if (transactionId > 0) {
                    // Hesap bakiyesi güncelle
                    if (accountId != null) {
                        val account = bankAccountRepository.getAccountById(accountId)
                            ?: run {
                                _uiState.value = AddTransactionUiState.Error("Seçilen hesap bulunamadı")
                                return@launch
                            }

                        val newBalance = when {
                            isSaleFlow && type == TransactionType.INCOME -> account.balance + amount
                            isPurchaseFlow && type == TransactionType.EXPENSE -> account.balance - amount
                            // Borçlu satın alım: DEBT + purchase akışı varsa kasadan düş (negatif izinli)
                            isPurchaseFlow && type == TransactionType.DEBT -> account.balance - amount
                            else -> account.balance
                        }
                        bankAccountRepository.updateBalance(account.id, newBalance)
                    }
                    _uiState.value = AddTransactionUiState.Success
                } else {
                    _uiState.value = AddTransactionUiState.Error("İşlem eklenirken hata oluştu")
                }
            } catch (e: Exception) {
                _uiState.value = AddTransactionUiState.Error("İşlem eklenirken hata oluştu: ${e.message}")
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