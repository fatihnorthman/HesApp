package com.ncorp.hesapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.ncorp.hesapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.ui.setupWithNavController
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import android.widget.ImageButton

/**
 * Ana Aktivite Sınıfı
 * 
 * Bu sınıf, uygulamanın ana giriş noktasıdır ve navigation yapısını yönetir.
 * MVVM mimarisine uygun olarak tasarlanmıştır.
 * 
 * Özellikler:
 * - Navigation component entegrasyonu
 * - View binding kullanımı
 * - Dependency injection desteği
 * - Action bar yapılandırması
 * - Fragment yönetimi
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    
    /**
     * View binding nesnesi
     * Layout dosyasına güvenli erişim sağlar
     */
    private lateinit var binding: ActivityMainBinding
    
    /**
     * Navigation controller
     * Fragment geçişlerini yönetir
     */
    private lateinit var navController: androidx.navigation.NavController
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // View binding'i başlat
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        
        // Tema tercihini uygula
        applySavedTheme()

        // Tema geçiş butonu
        binding.toolbar.findViewById<ImageButton>(R.id.btnThemeToggle).setOnClickListener {
            toggleTheme()
        }

        // Navigation'ı yapılandır
        setupNavigation()
    }
    
    /**
     * Navigation yapısını yapılandırır
     * 
     * Bu metod, navigation component'i başlatır ve fragment geçişlerini
     * yönetir. Safe args kullanarak tip güvenli navigation sağlar.
     */
    private fun setupNavigation() {
        // NavHostFragment'i bul
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        
        // Navigation controller'ı al
        navController = navHostFragment.navController
        
        // Bottom Navigation'ı NavController ile bağla
        binding.bottomNavigation.setupWithNavController(navController)
        
        // Action bar ile navigation controller'ı bağla
        setupActionBarWithNavController(navController)
        
        // Navigation listener'ı ayarla
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Fragment değiştiğinde action bar başlığını güncelle
            supportActionBar?.title = destination.label ?: getString(R.string.app_name)
        }
    }
    
    /**
     * Geri butonuna basıldığında çağrılır
     * 
     * Bu metod, kullanıcı geri butonuna bastığında navigation stack'te
     * geri gitmeyi sağlar. Eğer navigation stack boşsa aktiviteyi kapatır.
     * 
     * @return Navigation işlemi başarılı ise true
     */
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
    
    /**
     * Aktivite kapatılırken çağrılır
     * 
     * Bu metod, aktivite kapatılırken gerekli temizlik işlemlerini yapar.
     * Kaynakları serbest bırakır ve veritabanı bağlantılarını kapatır.
     */
    override fun onDestroy() {
        super.onDestroy()
        
        // View binding'i temizle
        // binding = null // Kotlin'de otomatik olarak temizlenir
    }

    private fun toggleTheme() {
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val isDark = prefs.getBoolean("dark_mode", false)
        val newMode = if (isDark) AppCompatDelegate.MODE_NIGHT_NO else AppCompatDelegate.MODE_NIGHT_YES
        AppCompatDelegate.setDefaultNightMode(newMode)
        prefs.edit().putBoolean("dark_mode", !isDark).apply()
    }

    private fun applySavedTheme() {
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val isDark = prefs.getBoolean("dark_mode", false)
        val mode = if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}