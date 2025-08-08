package com.ncorp.hesapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.core.os.bundleOf
import com.ncorp.hesapp.R
import com.ncorp.hesapp.databinding.FragmentDashboardBinding
import com.ncorp.hesapp.ui.viewmodel.DashboardViewModel
import com.ncorp.hesapp.utils.ToastUtils
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

    private val viewModel: DashboardViewModel by viewModels()

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
        
        // Fragment giriş animasyonu
        val fadeInAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        view.startAnimation(fadeInAnimation)
        
        // UI'ı yapılandır
        setupUI()
        
        // Observer'ları ayarla
        setupObservers()
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
        // Hızlı işlemleri yukarıya taşıma: layout zaten card sıralı; burada yeni satış butonunu ekliyoruz
        binding.btnAddIncome.setOnClickListener {
            val scaleAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.button_scale)
            binding.btnAddIncome.startAnimation(scaleAnimation)
            findNavController().navigate(R.id.action_dashboard_to_addTransaction)
        }
        binding.btnAddExpense.setOnClickListener {
            val scaleAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.button_scale)
            binding.btnAddExpense.startAnimation(scaleAnimation)
            findNavController().navigate(R.id.action_dashboard_to_addTransaction)
        }
        binding.btnAddContact.setOnClickListener {
            val scaleAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.button_scale)
            binding.btnAddContact.startAnimation(scaleAnimation)
            findNavController().navigate(R.id.action_dashboard_to_contacts)
        }
        binding.btnViewReports.setOnClickListener {
            val scaleAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.button_scale)
            binding.btnViewReports.startAnimation(scaleAnimation)
            findNavController().navigate(R.id.action_dashboard_to_reports)
        }
        // Eğer layout'ta btnAddSale varsa
        val btnAddSale = binding.root.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnAddSale)
        btnAddSale?.setOnClickListener {
            val scaleAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.button_scale)
            btnAddSale.startAnimation(scaleAnimation)
            // Satış akışı için AddTransaction'a gidip ürün seçim diyaloğunu aç
            val args = bundleOf("preselectFlow" to "sale")
            findNavController().navigate(R.id.action_dashboard_to_addTransaction, args)
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
            viewModel.loadDashboardData()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    /**
     * Observer'ları ayarlar
     * 
     * Bu metod, ViewModel'den gelen verileri dinler ve UI'ı günceller.
     */
    private fun setupObservers() {
        viewModel.dashboardData.observe(viewLifecycleOwner) { data -> updateDashboardUI(data) }

        viewModel.isLoading.observe(viewLifecycleOwner) { /* TODO loading */ }

        viewModel.error.observe(viewLifecycleOwner) { it?.let { ToastUtils.showErrorSnackbar(binding.root, it); viewModel.clearError() } }
    }

    /**
     * Dashboard UI'ını günceller
     * 
     * Bu metod, ViewModel'den gelen verileri kullanarak UI'ı günceller.
     */
    private fun updateDashboardUI(data: DashboardViewModel.DashboardData) {
        // Toplam gelir
        binding.tvTotalIncome.text = data.totalIncome
        
        // Toplam gider
        binding.tvTotalExpense.text = data.totalExpense
        
        // Net durum
        binding.tvNetAmount.text = data.netAmount
        
        // Borç/Alacak sonrası net durum
        binding.tvNetAmountAfterDebt.text = "Borç/Alacak Sonrası Net: ${data.netAmountAfterDebt}"
        
        // Toplam borç
        binding.tvTotalDebt.text = data.totalDebt
        
        // Toplam alacak
        binding.tvTotalReceivable.text = data.totalReceivable
        
        // Son işlemler sayısı
        binding.tvRecentTransactions.text = data.recentTransactions
        
        // Toplam işlem sayısı
        binding.tvTotalTransactions.text = data.totalTransactions
        
        // Toplam kişi sayısı
        binding.tvTotalContacts.text = data.totalContacts

        // Net durum rengini ayarla
        updateNetAmountColor(data.rawNetAmount)
        updateNetAmountAfterDebtColor(data.rawNetAmountAfterDebt)
    }

    private fun updateNetAmountAfterDebtColor(netAmountAfterDebt: Double) {
        val colorRes = if (netAmountAfterDebt >= 0) {
            R.color.success
        } else {
            R.color.error_light
        }
        binding.tvNetAmountAfterDebt.setTextColor(resources.getColor(colorRes, null))
    }

    private fun updateNetAmountColor(netAmount: Double) {
        val colorRes = if (netAmount >= 0) {
            R.color.success
        } else {
            R.color.error_light
        }
        binding.tvNetAmount.setTextColor(resources.getColor(colorRes, null))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // View binding'i temizle
        _binding = null
    }
} 