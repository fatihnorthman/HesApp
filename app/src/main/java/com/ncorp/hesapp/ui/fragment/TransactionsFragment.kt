package com.ncorp.hesapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.ncorp.hesapp.R
import com.ncorp.hesapp.data.model.TransactionType
import com.ncorp.hesapp.databinding.FragmentTransactionsBinding
import com.ncorp.hesapp.ui.adapter.TransactionAdapter
import com.ncorp.hesapp.ui.viewmodel.TransactionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/*
 * TransactionsFragment.kt
 *
 * Bu dosya, uygulamanın tüm finansal işlemlerini listeleyen ve yöneten ekranı (fragment) içerir.
 * Fragment, Android'de bir ekranın veya ekran parçasının mantığını ve arayüzünü yöneten yapılardır.
 * Bu ekran, işlemleri listeleme, filtreleme, arama, ekleme, düzenleme ve silme gibi işlevleri içerir.
 *
 * Kullanılan teknolojiler:
 * - MVVM mimarisi: Kodun okunabilirliğini ve sürdürülebilirliğini artırır.
 * - View Binding: XML arayüz dosyalarına güvenli erişim sağlar.
 * - Hilt: Bağımlılık enjeksiyonu ile modüler ve test edilebilir kod yazmayı sağlar.
 * - RecyclerView: Büyük veri listelerini performanslı şekilde göstermek için kullanılır.
 * - SwipeRefreshLayout: Kullanıcının ekranı aşağı çekerek verileri yenilemesini sağlar.
 * - Snackbar: Kullanıcıya kısa mesajlar göstermek için kullanılır.
 *
 * Kodun her adımı, "neden böyle yapıldı?" ve "ne işe yarar?" sorularına cevap verecek şekilde açıklanmıştır.
 */
@AndroidEntryPoint
class TransactionsFragment : Fragment() {

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TransactionViewModel by viewModels()
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        setupObservers()
        setupListeners()
    }

    private fun setupUI() {
        setupRecyclerView()
        setupSearchView()
        setupFilterChips()
        setupFloatingActionButton()
        setupSwipeRefresh()
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter(
            onItemClick = { transaction ->
                // TODO: İşlem detay sayfasına yönlendir
                showSnackbar("İşlem seçildi: ${transaction.description}")
            },
            onItemLongClick = { transaction ->
                // TODO: İşlem düzenleme/silme dialog'u göster
                showSnackbar("İşlem düzenleme: ${transaction.description}")
                true
            }
        )

        binding.recyclerView.apply {
            adapter = transactionAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.setSearchQuery(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setSearchQuery(newText ?: "")
                return true
            }
        })
    }

    private fun setupFilterChips() {
        binding.chipAll.setOnClickListener {
            viewModel.setFilter(null)
            updateChipSelection(binding.chipAll)
        }

        binding.chipIncome.setOnClickListener {
            viewModel.setFilter(TransactionType.INCOME)
            updateChipSelection(binding.chipIncome)
        }

        binding.chipExpense.setOnClickListener {
            viewModel.setFilter(TransactionType.EXPENSE)
            updateChipSelection(binding.chipExpense)
        }

        binding.chipDebt.setOnClickListener {
            viewModel.setFilter(TransactionType.DEBT)
            updateChipSelection(binding.chipDebt)
        }

        binding.chipReceivable.setOnClickListener {
            viewModel.setFilter(TransactionType.RECEIVABLE)
            updateChipSelection(binding.chipReceivable)
        }
    }

    private fun updateChipSelection(selectedChip: Chip) {
        // Tüm chip'leri temizle
        binding.chipAll.isChecked = false
        binding.chipIncome.isChecked = false
        binding.chipExpense.isChecked = false
        binding.chipDebt.isChecked = false
        binding.chipReceivable.isChecked = false

        // Seçili chip'i işaretle
        selectedChip.isChecked = true
    }

    private fun setupFloatingActionButton() {
        binding.fabAddTransaction.setOnClickListener {
            findNavController().navigate(R.id.action_transactions_to_addTransaction)
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshData()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { uiState ->
                updateUI(uiState)
            }
        }
    }

    private fun setupListeners() {
        // Test için örnek veriler ekleme (geliştirme aşamasında)
        binding.emptyState.setOnClickListener {
            viewModel.insertSampleData()
        }
    }

    private fun updateUI(uiState: com.ncorp.hesapp.ui.viewmodel.TransactionUiState) {
        // Loading state
        if (uiState.isLoading) {
            binding.recyclerView.visibility = View.GONE
            binding.emptyState.visibility = View.GONE
            // TODO: Loading indicator göster
        } else {
            // İşlem listesi güncelle
            transactionAdapter.submitList(uiState.transactions)

            // Empty state kontrolü
            if (uiState.isEmpty) {
                binding.recyclerView.visibility = View.GONE
                binding.emptyState.visibility = View.VISIBLE
            } else {
                binding.recyclerView.visibility = View.VISIBLE
                binding.emptyState.visibility = View.GONE
            }
        }

        // Error handling
        uiState.error?.let { error ->
            showSnackbar(error)
            viewModel.clearError()
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 