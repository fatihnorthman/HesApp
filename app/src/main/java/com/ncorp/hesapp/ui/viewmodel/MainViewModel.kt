package com.ncorp.hesapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Ana ViewModel Sınıfı
 * 
 * Bu sınıf, uygulamanın ana iş mantığını yönetir ve UI ile veri katmanı arasında
 * köprü görevi görür. Tüm işlemler ve kişi yönetimi bu ViewModel üzerinden yapılır.
 * 
 * Özellikler:
 * - İşlem yönetimi (CRUD işlemleri)
 * - Kişi yönetimi (CRUD işlemleri)
 * - İstatistik hesaplamaları
 * - Arama ve filtreleme
 * - Hata yönetimi
 * - Loading durumları
 * - Dependency injection desteği
 */
@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    
    // TODO: Room ve Repository implementasyonu eklenecek
    
    /**
     * Para formatını düzenler (kuruş -> TL)
     */
    fun formatCurrency(amountInKurus: Long): String {
        val lira = amountInKurus / 100
        val kurus = amountInKurus % 100
        return String.format("%d,%02d ₺", lira, kurus)
    }
    
    /**
     * Kuruş formatını düzenler (TL -> kuruş)
     */
    fun parseCurrency(currencyString: String): Long {
        return try {
            val cleanString = currencyString.replace(Regex("[^0-9,.]"), "")
            val parts = cleanString.split(",")
            val lira = parts[0].toLong()
            val kurus = if (parts.size > 1) parts[1].toLong() else 0L
            lira * 100 + kurus
        } catch (e: Exception) {
            0L
        }
    }
} 