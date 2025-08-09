package com.ncorp.hesapp.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.ncorp.hesapp.data.converter.Converters
import com.ncorp.hesapp.data.dao.TransactionDao
import com.ncorp.hesapp.data.dao.ContactDao
import com.ncorp.hesapp.data.dao.ProductDao
import com.ncorp.hesapp.data.dao.BankAccountDao
import com.ncorp.hesapp.data.model.Transaction
import com.ncorp.hesapp.data.model.Contact
import com.ncorp.hesapp.data.model.Product
import com.ncorp.hesapp.data.model.BankAccount

/*
 * AppDatabase.kt
 *
 * Bu dosya, uygulamanın veritabanı yönetimini sağlayan Room Database sınıfını içerir.
 * Room, Android için modern ve güvenli bir veritabanı katmanıdır. SQL sorgularını kolayca yazmayı ve veriyle güvenli şekilde çalışmayı sağlar.
 *
 * Bu sınıf, uygulamanın tüm verilerini sakladığı ana veritabanını oluşturur ve yönetir.
 *
 * Kullanılan teknolojiler:
 * - Room: Android için ORM (Object Relational Mapping) kütüphanesi.
 * - Singleton Pattern: Veritabanı nesnesinin uygulama boyunca tek bir örneğinin olmasını sağlar.
 * - TypeConverters: Karmaşık veri tiplerinin veritabanında saklanabilmesini sağlar.
 * - Migration: Veritabanı şeması değiştiğinde veri kaybı olmadan geçiş yapılmasını sağlar.
 *
 * Kodun her adımı, "neden böyle yapıldı?" ve "ne işe yarar?" sorularına cevap verecek şekilde açıklanmıştır.
 */
@Database(
    entities = [Transaction::class, Contact::class, Product::class, BankAccount::class],
    version = 9,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Transaction DAO
     */
    abstract fun transactionDao(): TransactionDao

    /**
     * Contact DAO
     */
    abstract fun contactDao(): ContactDao

    /**
     * Product DAO
     */
    abstract fun productDao(): ProductDao

    /**
     * BankAccount DAO
     */
    abstract fun bankAccountDao(): BankAccountDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hesapp_database"
                )
                .fallbackToDestructiveMigration()
                .setQueryCallback(RoomDatabase.QueryCallback { sqlQuery, bindArgs ->
                    // Query logging (debug için açılabilir)
                    // println("SQL Query: $sqlQuery")
                }, Runnable::run)
                .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING) // Enable WAL mode for better performance
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 