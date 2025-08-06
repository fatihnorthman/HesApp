package com.ncorp.hesapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import java.util.Date

/**
 * Transaction Entity
 * 
 * İşlem verilerini temsil eden data class.
 * Room veritabanı entegrasyonu için Entity annotation'ı kullanılır.
 * 
 * Özellikler:
 * - İşlem ID'si (Primary Key)
 * - İşlem türü (Gelir, Gider, Borç, Alacak)
 * - Açıklama
 * - Kategori
 * - Tutar
 * - Tarih
 * - İlişkili kişi ID'si
 * - Notlar
 */
@Entity(
    tableName = "transactions",
    indices = [
        Index(value = ["type"]),
        Index(value = ["date"]),
        Index(value = ["category"]),
        Index(value = ["contactId"]),
        Index(value = ["type", "date"])  // Composite index for common queries
    ]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val type: TransactionType,
    val description: String,
    val category: String,
    val amount: Double,
    val date: Date,
    val contactId: Long? = null,
    val notes: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

/**
 * Transaction Type Enum
 * 
 * İşlem türlerini tanımlar.
 */
enum class TransactionType {
    INCOME,     // Gelir
    EXPENSE,    // Gider
    DEBT,       // Borç
    RECEIVABLE  // Alacak
}

/**
 * Transaction Type Extensions
 * 
 * TransactionType enum'ı için yardımcı fonksiyonlar.
 */
fun TransactionType.getDisplayName(): String {
    return when (this) {
        TransactionType.INCOME -> "Gelir"
        TransactionType.EXPENSE -> "Gider"
        TransactionType.DEBT -> "Borç"
        TransactionType.RECEIVABLE -> "Alacak"
    }
}

fun TransactionType.getColorRes(): Int {
    return when (this) {
        TransactionType.INCOME -> android.R.color.holo_green_light
        TransactionType.EXPENSE -> android.R.color.holo_red_light
        TransactionType.DEBT -> android.R.color.holo_orange_light
        TransactionType.RECEIVABLE -> android.R.color.holo_blue_light
    }
}

fun TransactionType.getGradientRes(): Int {
    return when (this) {
        TransactionType.INCOME -> com.ncorp.hesapp.R.drawable.gradient_income
        TransactionType.EXPENSE -> com.ncorp.hesapp.R.drawable.gradient_expense
        TransactionType.DEBT -> com.ncorp.hesapp.R.drawable.gradient_debt
        TransactionType.RECEIVABLE -> com.ncorp.hesapp.R.drawable.gradient_receivable
    }
} 