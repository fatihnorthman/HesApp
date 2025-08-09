package com.ncorp.hesapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.ncorp.hesapp.R
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.datepicker.MaterialDatePicker
import com.ncorp.hesapp.ui.adapter.TransactionAdapter
import com.ncorp.hesapp.ui.viewmodel.ReportsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.ncorp.hesapp.databinding.FragmentReportsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportsFragment : Fragment() {

    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ReportsViewModel by viewModels()
    private lateinit var adapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Fragment giriş animasyonu
        val fadeInAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        view.startAnimation(fadeInAnimation)
        
        setupUi()
        observeViewModel()
    }

    private fun setupUi() {
        adapter = TransactionAdapter(onItemClick = {}, onItemLongClick = { false }, onDeleteClick = {})
        binding.recyclerView.adapter = adapter

        // Tarih seçimi
        val datePicker = MaterialDatePicker.Builder.dateRangePicker().setTitleText("Tarih Aralığı Seçin").build()
        val openPicker = View.OnClickListener {
            datePicker.show(parentFragmentManager, "dateRange")
        }
        binding.etStartDate.setOnClickListener(openPicker)
        binding.etEndDate.setOnClickListener(openPicker)
        datePicker.addOnPositiveButtonClickListener { range ->
            val start = java.util.Date(range.first ?: 0L)
            val end = java.util.Date(range.second ?: 0L)
            binding.etStartDate.setText(java.text.SimpleDateFormat("dd.MM.yyyy").format(start))
            binding.etEndDate.setText(java.text.SimpleDateFormat("dd.MM.yyyy").format(end))
            viewModel.setDateRange(start, end)
        }

        binding.chipIncomeStatement.setOnCheckedChangeListener { _, isChecked -> if (isChecked) viewModel.setReportType(ReportsViewModel.ReportType.INCOME_STATEMENT) }
        binding.chipCashFlow.setOnCheckedChangeListener { _, isChecked -> if (isChecked) viewModel.setReportType(ReportsViewModel.ReportType.CASH_FLOW) }
        binding.chipAging.setOnCheckedChangeListener { _, isChecked -> if (isChecked) viewModel.setReportType(ReportsViewModel.ReportType.AGING) }
        binding.chipIncomeStatement.isChecked = true
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                when (state) {
                    is ReportsViewModel.ReportUiState.Empty -> {
                        binding.tvSummary.text = "Veri bulunamadı"
                        adapter.submitList(emptyList())
                    }
                    is ReportsViewModel.ReportUiState.IncomeStatement -> {
                        binding.tvSummary.text = "Gelir: %.2f\nGider: %.2f\nNet: %.2f".format(state.income, state.expense, state.net)
                        adapter.submitList(state.items)
                    }
                    is ReportsViewModel.ReportUiState.CashFlow -> {
                        binding.tvSummary.text = "Giriş: %.2f\nÇıkış: %.2f\nNet: %.2f".format(state.inflow, state.outflow, state.net)
                        adapter.submitList(state.items)
                    }
                    is ReportsViewModel.ReportUiState.Aging -> {
                        binding.tvSummary.text = "Borç: %.2f\nAlacak: %.2f".format(state.debts.sumOf { it.amount }, state.receivables.sumOf { it.amount })
                        adapter.submitList(state.debts + state.receivables)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 