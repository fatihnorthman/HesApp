package com.ncorp.hesapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ncorp.hesapp.databinding.FragmentDashboardBinding
import dagger.hilt.android.AndroidEntryPoint
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

/*
 * DashboardFragment.kt
 *
 * Bu dosya, uygulamanın ana ekranı olan Dashboard ekranını yönetir.
 * Fragment, Android'de bir ekranın veya ekran parçasının mantığını ve arayüzünü yöneten yapılardır.
 * Dashboard ekranı, kullanıcının finansal özetini, hızlı aksiyon butonlarını ve son işlemleri gösterir.
 *
 * Kullanılan teknolojiler:
 * - MVVM mimarisi: Kodun okunabilirliğini ve sürdürülebilirliğini artırır.
 * - View Binding: XML arayüz dosyalarına güvenli erişim sağlar.
 * - Hilt: Bağımlılık enjeksiyonu ile modüler ve test edilebilir kod yazmayı sağlar.
 * - SwipeRefreshLayout: Kullanıcının ekranı aşağı çekerek verileri yenilemesini sağlar.
 *
 * Kodun her adımı, "neden böyle yapıldı?" ve "ne işe yarar?" sorularına cevap verecek şekilde açıklanmıştır.
 */
@AndroidEntryPoint
class DashboardFragment : Fragment() {

    /**
     * View binding nesnesi
     * Layout dosyasına güvenli erişim sağlar
     */
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // View binding'i başlat
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // UI'ı yapılandır
        setupUI()
        
        // Örnek verileri göster
        showSampleData()
    }

    /**
     * UI bileşenlerini yapılandırır
     * 
     * Bu metod, fragment'taki UI bileşenlerini ayarlar ve
     * kullanıcı etkileşimlerini yönetir.
     */
    private fun setupUI() {
        // Hızlı işlem butonlarını ayarla
        setupQuickActionButtons()
        
        // Swipe refresh layout'u ayarla
        setupSwipeRefresh()
    }

    /**
     * Hızlı işlem butonlarını yapılandırır
     * 
     * Bu metod, dashboard'daki hızlı işlem butonlarını ayarlar
     * ve tıklama olaylarını yönetir.
     */
    private fun setupQuickActionButtons() {
        // Gelir ekleme butonu
        binding.btnAddIncome.setOnClickListener {
            // TODO: Gelir ekleme sayfasına yönlendir
        }

        // Gider ekleme butonu
        binding.btnAddExpense.setOnClickListener {
            // TODO: Gider ekleme sayfasına yönlendir
        }

        // Kişi ekleme butonu
        binding.btnAddContact.setOnClickListener {
            // TODO: Kişi ekleme sayfasına yönlendir
        }

        // Rapor görüntüleme butonu
        binding.btnViewReports.setOnClickListener {
            // TODO: Raporlar sayfasına yönlendir
        }
    }

    /**
     * Swipe refresh layout'u yapılandırır
     * 
     * Bu metod, kullanıcının aşağı çekerek verileri yenilemesini sağlar.
     */
    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            // Verileri yenile
            showSampleData()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    /**
     * Örnek verileri gösterir
     * 
     * Bu metod, dashboard'da örnek verileri gösterir.
     */
    private fun showSampleData() {
        // Toplam gelir
        binding.tvTotalIncome.text = "15.750 ₺"
        
        // Toplam gider
        binding.tvTotalExpense.text = "8.250 ₺"
        
        // Net durum
        binding.tvNetAmount.text = "7.500 ₺"
        
        // Toplam borç
        binding.tvTotalDebt.text = "2.500 ₺"
        
        // Toplam alacak
        binding.tvTotalReceivable.text = "1.800 ₺"
        
        // Son işlemler sayısı
        binding.tvRecentTransactions.text = "24"
        
        // Toplam kişi sayısı
        binding.tvTotalContacts.text = "12"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // View binding'i temizle
        _binding = null
    }
} 