package com.ncorp.hesapp

import android.app.Application
import android.os.StrictMode

import com.ncorp.hesapp.utils.PerformanceMonitor
import dagger.hilt.android.HiltAndroidApp

/*
 * HesAppApplication.kt
 *
 * Bu dosya, uygulamanın Application sınıfını içerir.
 * Android'de Application sınıfı, uygulama başlatıldığında ilk çalışan ve uygulama boyunca yaşayan ana sınıftır.
 * Burada, uygulama genelinde kullanılacak bağımlılıkların (örneğin veritabanı, repository, servisler) yönetimi yapılır.
 *
 * Kullanılan teknolojiler:
 * - Dagger Hilt: Bağımlılık enjeksiyonu için kullanılır. @HiltAndroidApp ile işaretlenen bu sınıf, Hilt'in uygulama genelinde çalışmasını sağlar.
 * - PerformanceMonitor: Uygulamanın performansını ve bellek kullanımını izlemek için kullanılır.
 * - StrictMode: Geliştirme aşamasında hatalı veya yavaş kodları tespit etmek için kullanılır.
 *
 * Bu dosyada, her fonksiyonun ve önemli kod bloğunun üstünde detaylı açıklamalar bulacaksınız.
 * Kodun her adımı, "neden böyle yapıldı?" ve "ne işe yarar?" sorularına cevap verecek şekilde açıklanmıştır.
 */
@HiltAndroidApp
class HesAppApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Performance monitoring başlat
        PerformanceMonitor.markAppStart()
        
        // Debug modunda StrictMode aktif et
        enableStrictMode()
        
        // Profile guided optimization başlat
        // ProfileInstaller.writeProfile(this) // Kaldırıldı, otomatik çalışır
        
        // Memory optimization
        PerformanceMonitor.logMemoryUsage("Application.onCreate")
        
        PerformanceMonitor.markAppReady("Application initialized")
    }
    
    /**
     * StrictMode'u aktif eder (debug build'ler için)
     */
    private fun enableStrictMode() {
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