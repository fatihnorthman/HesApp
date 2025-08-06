package com.ncorp.hesapp.di

import android.content.Context
import com.ncorp.hesapp.data.database.AppDatabase
import com.ncorp.hesapp.data.dao.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Database Module
 * 
 * Hilt dependency injection modülü.
 * Veritabanı bağımlılıklarını sağlar.
 * 
 * Özellikler:
 * - Database instance
 * - DAO'lar
 * - Singleton pattern
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * AppDatabase instance'ı sağla (Memory optimized)
     */
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }
    
    /**
     * Database executor for background operations
     */
    @Provides
    @Singleton
    fun provideDatabaseExecutor(): java.util.concurrent.Executor {
        return java.util.concurrent.Executors.newFixedThreadPool(4)
    }

    /**
     * TransactionDao instance'ı sağla
     */
    @Provides
    @Singleton
    fun provideTransactionDao(database: AppDatabase): TransactionDao {
        return database.transactionDao()
    }
} 