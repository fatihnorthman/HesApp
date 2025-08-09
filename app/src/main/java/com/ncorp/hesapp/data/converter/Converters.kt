/*
 * Converters.kt
 *
 * Bu dosya, Room veritabanı için özel tip dönüştürücüleri (TypeConverter) içerir.
 * Room, sadece temel veri tiplerini (String, Int, Long, vb.) doğrudan saklayabilir.
 * Eğer Date veya enum gibi özel tipler kullanıyorsak, bunları veritabanında saklanabilir bir tipe (ör. Long, String) dönüştürmek gerekir.
 *
 * Temel görevleri:
 * - Date <-> Long dönüşümü (tarihleri milisaniye olarak saklamak için)
 * - TransactionType <-> String dönüşümü (enum'u string olarak saklamak için)
 * - Null güvenliği sağlamak
 *
 * Kodun her adımı, "neden böyle yapıldı?" ve "ne işe yarar?" sorularına cevap verecek şekilde açıklanmıştır.
 */
package com.ncorp.hesapp.data.converter

import androidx.room.TypeConverter
import com.ncorp.hesapp.data.model.TransactionType
import com.ncorp.hesapp.data.model.Currency
import com.ncorp.hesapp.data.model.AccountType
import java.util.Date

/**
 * Room Type Converters
 * 
 * Room veritabanı için tip dönüştürücüleri.
 * 
 * Özellikler:
 * - Date <-> Long dönüşümü
 * - TransactionType <-> String dönüşümü
 * - Null safety
 */
class Converters {

    /**
     * Date'i Long'a dönüştür
     */
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    /**
     * Long'u Date'e dönüştür
     */
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    /**
     * TransactionType'ı String'e dönüştür
     */
    @TypeConverter
    fun fromTransactionType(type: TransactionType?): String? {
        return type?.name
    }

    /**
     * String'i TransactionType'a dönüştür
     */
    @TypeConverter
    fun toTransactionType(value: String?): TransactionType? {
        return value?.let { TransactionType.valueOf(it) }
    }

    /**
     * Currency'i String'e dönüştür
     */
    @TypeConverter
    fun fromCurrency(currency: Currency?): String? {
        return currency?.name
    }

    /**
     * String'i Currency'e dönüştür
     */
    @TypeConverter
    fun toCurrency(value: String?): Currency? {
        return value?.let { Currency.valueOf(it) }
    }

    /**
     * AccountType'i String'e dönüştür
     */
    @TypeConverter
    fun fromAccountType(accountType: AccountType?): String? {
        return accountType?.name
    }

    /**
     * String'i AccountType'e dönüştür
     */
    @TypeConverter
    fun toAccountType(value: String?): AccountType? {
        return value?.let { AccountType.valueOf(it) }
    }
} 