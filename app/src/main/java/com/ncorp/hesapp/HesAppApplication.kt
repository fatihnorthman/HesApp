package com.ncorp.hesapp

import android.app.Application
import android.os.StrictMode
import androidx.profileinstaller.ProfileInstaller
import com.ncorp.hesapp.utils.PerformanceMonitor
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
        
        // Performance monitoring başlat
        PerformanceMonitor.markAppStart()
        
        // Debug modunda StrictMode aktif et
        if (BuildConfig.DEBUG) {
            enableStrictMode()
        }
        
        // Profile guided optimization başlat
        ProfileInstaller.writeProfile(this)
        
        // Memory optimization
        PerformanceMonitor.logMemoryUsage("Application.onCreate")
        
        PerformanceMonitor.markAppReady("Application initialized")
    }
    
    /**
     * StrictMode'u aktif eder (debug build'ler için)
     */
    private fun enableStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .detectCustomSlowCalls()
                    .penaltyLog()
                    .build()
            )
            
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .detectActivityLeaks()
                    .detectLeakedRegistrationObjects()
                    .penaltyLog()
                    .build()
            )
        }
    }
} 