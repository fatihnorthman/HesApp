package com.ncorp.hesapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ncorp.hesapp.databinding.FragmentTransactionsBinding
import dagger.hilt.android.AndroidEntryPoint
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

/**
 * Transactions Fragment
 * 
 * Bu fragment, tüm işlemleri listeler ve yönetir.
 * MVVM mimarisine uygun olarak tasarlanmıştır.
 * 
 * Özellikler:
 * - İşlem listesi görüntüleme
 * - İşlem türüne göre filtreleme
 * - Arama fonksiyonu
 * - İşlem ekleme/düzenleme/silme
 * - Swipe refresh
 * - RecyclerView ile performanslı liste
 */
@AndroidEntryPoint
class TransactionsFragment : Fragment() {

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!

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
    }

    private fun setupUI() {
        setupSearchView()
        setupFilterChips()
        setupFloatingActionButton()
        setupSwipeRefresh()
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // TODO: Arama işlemi
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // TODO: Arama işlemi
                return true
            }
        })
    }

    private fun setupFilterChips() {
        binding.chipAll.setOnClickListener {
            // TODO: Tüm işlemleri göster
        }

        binding.chipIncome.setOnClickListener {
            // TODO: Gelir işlemlerini göster
        }

        binding.chipExpense.setOnClickListener {
            // TODO: Gider işlemlerini göster
        }

        binding.chipDebt.setOnClickListener {
            // TODO: Borç işlemlerini göster
        }

        binding.chipReceivable.setOnClickListener {
            // TODO: Alacak işlemlerini göster
        }
    }

    private fun setupFloatingActionButton() {
        binding.fabAddTransaction.setOnClickListener {
            // TODO: İşlem ekleme sayfasına yönlendir
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            // TODO: Verileri yenile
            binding.swipeRefresh.isRefreshing = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 