package com.ncorp.hesapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ncorp.hesapp.databinding.FragmentContactsBinding
import dagger.hilt.android.AndroidEntryPoint
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

/**
 * Contacts Fragment
 * 
 * Bu fragment, tüm kişileri listeler ve yönetir.
 * MVVM mimarisine uygun olarak tasarlanmıştır.
 * 
 * Özellikler:
 * - Kişi listesi görüntüleme
 * - Kişi türüne göre filtreleme
 * - Arama fonksiyonu
 * - Kişi ekleme/düzenleme/silme
 * - Swipe refresh
 * - RecyclerView ile performanslı liste
 */
@AndroidEntryPoint
class ContactsFragment : Fragment() {

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
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
            // TODO: Tüm kişileri göster
        }

        binding.chipCustomer.setOnClickListener {
            // TODO: Müşterileri göster
        }

        binding.chipSupplier.setOnClickListener {
            // TODO: Tedarikçileri göster
        }

        binding.chipEmployee.setOnClickListener {
            // TODO: Çalışanları göster
        }
    }

    private fun setupFloatingActionButton() {
        binding.fabAddContact.setOnClickListener {
            // TODO: Kişi ekleme sayfasına yönlendir
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