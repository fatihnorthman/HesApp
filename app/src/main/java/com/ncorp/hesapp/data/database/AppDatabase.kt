package com.ncorp.hesapp.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.google.common.util.concurrent.MoreExecutors
import com.ncorp.hesapp.BuildConfig
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
                .setQueryCallback(RoomDatabase.QueryCallback { sqlQuery, bindArgs ->
                    // Query logging in debug mode only
                    if (BuildConfig.DEBUG) {
                        println("SQL Query: $sqlQuery")
                    }
                }, MoreExecutors.directExecutor())
                .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING) // Enable WAL mode for better performance
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 