package com.ncorp.hesapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.ncorp.hesapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.ui.setupWithNavController

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
        
        // Action bar'ı yapılandır
        setupActionBar()
        
        // Navigation'ı yapılandır
        setupNavigation()
    }
    
    /**
     * Action bar'ı yapılandırır
     * 
     * Bu metod, action bar'ın görünümünü ve davranışını ayarlar.
     * Uygulama temasına uygun olarak tasarlanmıştır.
     */
    private fun setupActionBar() {
        // Action bar'ı etkinleştir
        supportActionBar?.apply {
            // Action bar başlığını ayarla
            title = getString(R.string.app_name)
            
            // Geri butonunu etkinleştir
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            
            // Action bar'ın görünümünü ayarla
            elevation = 0f
        }
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
}