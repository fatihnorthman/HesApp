package com.ncorp.hesapp.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.ncorp.hesapp.data.converter.Converters
import com.ncorp.hesapp.data.dao.TransactionDao
import com.ncorp.hesapp.data.model.Transaction

/**
 * App Database
 * 
 * Room veritabanı sınıfı.
 * 
 * Özellikler:
 * - Transaction tablosu
 * - Type converter'lar
 * - Singleton pattern
 * - Migration desteği
 */
@Database(
    entities = [Transaction::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Transaction DAO
     */
    abstract fun transactionDao(): TransactionDao

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
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 