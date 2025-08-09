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
import android.animation.ObjectAnimator
import android.view.animation.DecelerateInterpolator

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
        val lite = requireContext().getSharedPreferences("settings", android.content.Context.MODE_PRIVATE)
            .getBoolean("lite_mode", false)
        if (!lite) {
            val fadeInAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
            view.startAnimation(fadeInAnimation)
        }
        
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

        // Kartlara tıklayınca büyütme animasyonu
        attachCardPopAnimation()
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
        binding.btnAccounts.setOnClickListener {
            val scaleAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.button_scale)
            binding.btnAccounts.startAnimation(scaleAnimation)
            findNavController().navigate(R.id.action_dashboard_to_accounts)
        }

        binding.btnPayment.setOnClickListener {
            val scaleAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.button_scale)
            binding.btnPayment.startAnimation(scaleAnimation)
            findNavController().navigate(R.id.action_dashboard_to_payment)
        }

        binding.btnCollection.setOnClickListener {
            val scaleAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.button_scale)
            binding.btnCollection.startAnimation(scaleAnimation)
            findNavController().navigate(R.id.action_dashboard_to_collection)
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

    private fun attachCardPopAnimation() {
        val cards = listOf(
            binding.root.findViewById<View>(R.id.tvTotalIncome)?.parent?.parent as? View,
            binding.root.findViewById<View>(R.id.tvTotalExpense)?.parent?.parent as? View,
            binding.root.findViewById<View>(R.id.tvNetAmount)?.parent?.parent?.parent as? View,
            binding.root.findViewById<View>(R.id.tvTotalDebt)?.parent?.parent as? View,
            binding.root.findViewById<View>(R.id.tvTotalReceivable)?.parent?.parent as? View
        ).filterNotNull()

        cards.forEach { card ->
            card.isClickable = true
            card.setOnClickListener {
                val lite = requireContext().getSharedPreferences("settings", android.content.Context.MODE_PRIVATE)
                    .getBoolean("lite_mode", false)
                if (lite) return@setOnClickListener
                val scaleUpX = ObjectAnimator.ofFloat(card, View.SCALE_X, 1f, 1.04f)
                val scaleUpY = ObjectAnimator.ofFloat(card, View.SCALE_Y, 1f, 1.04f)
                val scaleDownX = ObjectAnimator.ofFloat(card, View.SCALE_X, 1.04f, 1f)
                val scaleDownY = ObjectAnimator.ofFloat(card, View.SCALE_Y, 1.04f, 1f)
                listOf(scaleUpX, scaleUpY, scaleDownX, scaleDownY).forEach { anim ->
                    anim.duration = 120
                    anim.interpolator = DecelerateInterpolator()
                }
                scaleUpX.start(); scaleUpY.start()
                card.postDelayed({ scaleDownX.start(); scaleDownY.start() }, 120)
            }
        }
    }

    /**
     * Dashboard UI'ını günceller
     * 
     * Bu metod, ViewModel'den gelen verileri kullanarak UI'ı günceller.
     */
    private fun updateDashboardUI(data: DashboardViewModel.DashboardData) {
        // Para birimlerine göre verileri göster
        val currencyTexts = mutableListOf<String>()
        
        data.currencyData.forEach { (currency, currencyData) ->
            if (currencyData.totalIncome != 0.0 || currencyData.totalExpense != 0.0 || 
                currencyData.totalDebt != 0.0 || currencyData.totalReceivable != 0.0) {
                
                val symbol = currency.symbol
                currencyTexts.add(buildString {
                    append("${currency.displayName}:\n")
                    append("Gelir: ${symbol}${String.format("%.2f", currencyData.totalIncome)}\n")
                    append("Gider: ${symbol}${String.format("%.2f", currencyData.totalExpense)}\n")
                    append("Net: ${symbol}${String.format("%.2f", currencyData.netAmount)}\n")
                    if (currencyData.totalDebt != 0.0 || currencyData.totalReceivable != 0.0) {
                        append("Borç: ${symbol}${String.format("%.2f", currencyData.totalDebt)}\n")
                        append("Alacak: ${symbol}${String.format("%.2f", currencyData.totalReceivable)}\n")
                        append("Net (B/A Sonrası): ${symbol}${String.format("%.2f", currencyData.netAmountAfterDebt)}")
                    }
                })
            }
        }
        
        // Toplu metin gösterimi
        val tlData = data.currencyData[com.ncorp.hesapp.data.model.Currency.TRY] ?: DashboardViewModel.CurrencyData(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        val usdData = data.currencyData[com.ncorp.hesapp.data.model.Currency.USD] ?: DashboardViewModel.CurrencyData(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        val eurData = data.currencyData[com.ncorp.hesapp.data.model.Currency.EUR] ?: DashboardViewModel.CurrencyData(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        
        binding.tvTotalIncome.text = buildString {
            append("₺${String.format("%.2f", tlData.totalIncome)}")
            append("\n$${String.format("%.2f", usdData.totalIncome)}")
            append("\n€${String.format("%.2f", eurData.totalIncome)}")
        }
        
        binding.tvTotalExpense.text = buildString {
            append("₺${String.format("%.2f", tlData.totalExpense)}")
            append("\n$${String.format("%.2f", usdData.totalExpense)}")
            append("\n€${String.format("%.2f", eurData.totalExpense)}")
        }
        
        binding.tvNetAmount.text = buildString {
            append("₺${String.format("%.2f", tlData.netAmount)}")
            append("\n$${String.format("%.2f", usdData.netAmount)}")
            append("\n€${String.format("%.2f", eurData.netAmount)}")
        }
        
        binding.tvTotalDebt.text = buildString {
            append("₺${String.format("%.2f", tlData.totalDebt)}")
            append("\n$${String.format("%.2f", usdData.totalDebt)}")
            append("\n€${String.format("%.2f", eurData.totalDebt)}")
        }
        
        binding.tvTotalReceivable.text = buildString {
            append("₺${String.format("%.2f", tlData.totalReceivable)}")
            append("\n$${String.format("%.2f", usdData.totalReceivable)}")
            append("\n€${String.format("%.2f", eurData.totalReceivable)}")
        }
        
        binding.tvNetAmountAfterDebt.text = buildString {
            append("₺${String.format("%.2f", tlData.netAmountAfterDebt)}")
            append("\n$${String.format("%.2f", usdData.netAmountAfterDebt)}")
            append("\n€${String.format("%.2f", eurData.netAmountAfterDebt)}")
        }
        
        // Son işlemler sayısı
        binding.tvRecentTransactions.text = data.recentTransactions
        
        // Toplam işlem sayısı
        binding.tvTotalTransactions.text = data.totalTransactions
        
        // Toplam kişi sayısı
        binding.tvTotalContacts.text = data.totalContacts

        // Net durum rengini ayarla (sadece TL için)
        updateNetAmountColor(tlData.netAmount)
        updateNetAmountAfterDebtColor(tlData.netAmountAfterDebt)
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