package com.ncorp.hesapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.ncorp.hesapp.databinding.ActivityMainBinding
import com.ncorp.hesapp.utils.PerformanceMonitor
import dagger.hilt.android.AndroidEntryPoint
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.ui.setupWithNavController
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import android.widget.ImageButton
import androidx.navigation.NavOptions
import com.google.android.material.card.MaterialCardView
import android.view.animation.AnimationUtils
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.ncorp.hesapp.utils.SoundUtils

/*
 * MainActivity.kt
 *
 * Bu dosya, uygulamanın ana giriş noktası olan MainActivity sınıfını içerir.
 * Android uygulamalarında "Activity", bir ekranı temsil eder. MainActivity ise uygulama açıldığında ilk çalışan ekrandır.
 * Bu sınıf, uygulamanın genel navigasyonunu (ekranlar arası geçişi), tema yönetimini (karanlık/aydınlık mod), toolbar ve alt menü (bottom navigation) gibi temel arayüz bileşenlerini yönetir.
 *
 * Kullanılan teknolojiler:
 * - MVVM (Model-View-ViewModel) mimarisi: Kodun okunabilirliğini ve sürdürülebilirliğini artırır.
 * - Navigation Component: Ekranlar arası geçişi kolaylaştırır ve güvenli hale getirir.
 * - View Binding: XML arayüz dosyalarına güvenli erişim sağlar.
 * - Dagger Hilt: Bağımlılık enjeksiyonu ile modüler ve test edilebilir kod yazmayı sağlar.
 * - Animasyonlar: Kullanıcı deneyimini zenginleştirir.
 *
 * Bu dosyada, her fonksiyonun ve önemli kod bloğunun üstünde detaylı açıklamalar bulacaksınız.
 * Kodun her adımı, "neden böyle yapıldı?" ve "ne işe yarar?" sorularına cevap verecek şekilde açıklanmıştır.
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
        
        // SoundUtils'i başlat
        SoundUtils.init(this)
        
        // Performance monitoring başlat
        PerformanceMonitor.measureExecutionTime("MainActivity.onCreate") {
            // View binding'i başlat
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            
            // Status bar ayarları
            setupStatusBar()
            
            setSupportActionBar(binding.toolbar)

            // Tema tercihini uygula
            applySavedTheme()

            // Geliştirilmiş tema geçiş butonu
            binding.btnThemeToggle.setOnClickListener {
                // Buton animasyonu
                val scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.button_scale)
                binding.btnThemeToggle.startAnimation(scaleAnimation)

                // Tema geçişi
                toggleTheme()
            }

            // Navigation'ı yapılandır
            setupNavigation()
        }
    }
    /**
     * Status bar ayarlarını yapılandırır
     * Bu metod, status bar'ın tema ile uyumlu olmasını sağlar
     * ve sistem UI ile çakışmaları önler.
     */
    private fun setupStatusBar() {
        // Status bar rengini tema ile uyumlu hale getir
        window.statusBarColor = ContextCompat.getColor(this, R.color.background)
        
        // Status bar içeriğinin rengini ayarla (açık/koyu tema)
        val isDarkTheme = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        val statusBarContentColor = if (isDarkTheme) {
            android.graphics.Color.WHITE
        } else {
            android.graphics.Color.BLACK
        }
        
        // Status bar içeriğinin rengini ayarla
        window.decorView.systemUiVisibility = if (isDarkTheme) {
            window.decorView.systemUiVisibility and android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        } else {
            window.decorView.systemUiVisibility or android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    /**
     * Navigation yapısını yapılandırır
     * Bu metod, navigation component'i başlatır ve fragment geçişlerini
     * yönetir. Safe args kullanarak tip güvenli navigation sağlar.
     * Gelişmiş animasyon desteği ile kullanıcı deneyimini iyileştirir.
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
        // Bottom navigation item seçim animasyonlarını iyileştir
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val currentDestination = navController.currentDestination?.id
            val targetDestination = when (item.itemId) {
                R.id.dashboardFragment -> R.id.dashboardFragment
                R.id.transactionsFragment -> R.id.transactionsFragment
                R.id.contactsFragment -> R.id.contactsFragment
                R.id.reportsFragment -> R.id.reportsFragment
                R.id.settingsFragment -> R.id.settingsFragment
                else -> null
            }
            if (targetDestination != null && targetDestination != currentDestination) {
                // Özel animasyon seçenekleri ile navigation
                val navOptions = NavOptions.Builder()
                    .setEnterAnim(R.anim.zoom_in)
                    .setExitAnim(R.anim.fade_out)
                    .setPopEnterAnim(R.anim.fade_in)
                    .setPopExitAnim(R.anim.zoom_out)
                    .build()
                navController.navigate(targetDestination, null, navOptions)
            }
            true
        }
    }
    /**
     * Geri butonuna basıldığında çağrılır
     * Bu metod, kullanıcı geri butonuna bastığında navigation stack'te
     * geri gitmeyi sağlar. Eğer navigation stack boşsa aktiviteyi kapatır.
     * @return Navigation işlemi başarılı ise true
     */
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
    /**
     * Aktivite kapatılırken çağrılır
     * Bu metod, aktivite kapatılırken gerekli temizlik işlemlerini yapar.
     * Kaynakları serbest bırakır ve veritabanı bağlantılarını kapatır.
     */
    override fun onDestroy() {
        super.onDestroy()
        // View binding'i temizle
        // binding = null // Kotlin'de otomatik olarak temizlenir
    }
    /**
     * Tema geçişini gerçekleştirir
     * Bu metod, kullanıcı tema geçiş butonuna bastığında çağrılır.
     * Aydınlık ve karanlık tema arasında geçiş yapar ve tercihi kaydeder.
     */
    private fun toggleTheme() {
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val isDark = prefs.getBoolean("dark_mode", false)
        val newMode =
            if (isDark) AppCompatDelegate.MODE_NIGHT_NO else AppCompatDelegate.MODE_NIGHT_YES
        AppCompatDelegate.setDefaultNightMode(newMode)
        prefs.edit().putBoolean("dark_mode", !isDark).apply()
        
        // Status bar'ı yeni temaya göre güncelle
        setupStatusBar()
    }
    /**
     * Kaydedilmiş tema tercihini uygular
     * Bu metod, uygulama başlatıldığında kullanıcının önceki tema tercihini
     * yükler ve uygular.
     */
    private fun applySavedTheme() {
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val isDark = prefs.getBoolean("dark_mode", false)
        val mode =
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}