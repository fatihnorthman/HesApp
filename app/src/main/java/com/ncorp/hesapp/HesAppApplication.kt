package com.ncorp.hesapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * HesApp Application Class
 * 
 * Bu sınıf, Hilt dependency injection için gerekli olan Application sınıfıdır.
 * Tüm uygulama genelinde dependency injection'ı yönetir.
 * 
 * @HiltAndroidApp annotation'ı, Hilt'in bu sınıfı Application sınıfı olarak
 * kullanmasını sağlar ve dependency injection container'ını başlatır.
 */
@HiltAndroidApp
class HesAppApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Uygulama başlatıldığında yapılacak işlemler
    }
} 