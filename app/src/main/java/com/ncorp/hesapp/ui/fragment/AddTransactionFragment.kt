package com.ncorp.hesapp.ui.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.ncorp.hesapp.R
import com.ncorp.hesapp.data.model.TransactionType
import com.ncorp.hesapp.databinding.FragmentAddTransactionBinding
import com.ncorp.hesapp.ui.viewmodel.AddTransactionViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddTransactionFragment : Fragment() {

    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddTransactionViewModel by viewModels()

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private var selectedDate: Date = Date()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupListeners()
        observeViewModel()
    }

    private fun setupViews() {
        // Toolbar setup
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_clear -> {
                    clearForm()
                    true
                }
                else -> false
            }
        }

        // Set current date
        binding.etDate.setText(dateFormat.format(selectedDate))
    }

    private fun setupListeners() {
        // Transaction type selection - individual chip listeners
        binding.chipIncome.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                updateContactFieldVisibility(R.id.chipIncome)
            }
        }
        
        binding.chipExpense.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                updateContactFieldVisibility(R.id.chipExpense)
            }
        }
        
        binding.chipDebt.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                updateContactFieldVisibility(R.id.chipDebt)
            }
        }
        
        binding.chipReceivable.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                updateContactFieldVisibility(R.id.chipReceivable)
            }
        }

        // Date picker
        binding.etDate.setOnClickListener {
            showDatePicker()
        }

        // Contact selection (for debt/receivable)
        binding.etContact.setOnClickListener {
            // TODO: Show contact picker dialog
            Toast.makeText(context, "Kişi seçimi yakında eklenecek", Toast.LENGTH_SHORT).show()
        }

        // Save button
        binding.fabSave.setOnClickListener {
            saveTransaction()
        }
    }

    private fun updateContactFieldVisibility(chipId: Int?) {
        val isDebtOrReceivable = chipId == R.id.chipDebt || chipId == R.id.chipReceivable
        binding.tilContact.visibility = if (isDebtOrReceivable) View.VISIBLE else View.GONE
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance().apply { time = selectedDate }
        
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                selectedDate = calendar.time
                binding.etDate.setText(dateFormat.format(selectedDate))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun saveTransaction() {
        val description = binding.etDescription.text.toString().trim()
        val category = binding.etCategory.text.toString().trim()
        val amountText = binding.etAmount.text.toString().trim()
        val notes = binding.etNotes.text.toString().trim()

        // Validation
        if (description.isEmpty()) {
            binding.tilDescription.error = "Açıklama gerekli"
            return
        }

        if (category.isEmpty()) {
            binding.tilCategory.error = "Kategori gerekli"
            return
        }

        if (amountText.isEmpty()) {
            binding.tilAmount.error = "Tutar gerekli"
            return
        }

        val amount = try {
            amountText.toDouble()
        } catch (e: NumberFormatException) {
            binding.tilAmount.error = "Geçersiz tutar"
            return
        }

        if (amount <= 0) {
            binding.tilAmount.error = "Tutar 0'dan büyük olmalı"
            return
        }

        // Get selected transaction type
        val selectedChipId = binding.chipGroupTransactionType.checkedChipId
        val transactionType = when (selectedChipId) {
            R.id.chipIncome -> TransactionType.INCOME
            R.id.chipExpense -> TransactionType.EXPENSE
            R.id.chipDebt -> TransactionType.DEBT
            R.id.chipReceivable -> TransactionType.RECEIVABLE
            else -> {
                Toast.makeText(context, "İşlem türü seçin", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // Clear errors
        binding.tilDescription.error = null
        binding.tilCategory.error = null
        binding.tilAmount.error = null

        // Save transaction
        viewModel.addTransaction(
            type = transactionType,
            description = description,
            category = category,
            amount = amount,
            date = selectedDate,
            notes = notes.ifEmpty { null }
        )
    }

    private fun clearForm() {
        binding.etDescription.text?.clear()
        binding.etCategory.text?.clear()
        binding.etAmount.text?.clear()
        binding.etNotes.text?.clear()
        binding.etContact.text?.clear()
        binding.chipGroupTransactionType.clearCheck()
        binding.tilContact.visibility = View.GONE
        
        selectedDate = Date()
        binding.etDate.setText(dateFormat.format(selectedDate))
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AddTransactionViewModel.AddTransactionUiState.Loading -> {
                    binding.fabSave.isEnabled = false
                }
                is AddTransactionViewModel.AddTransactionUiState.Success -> {
                    binding.fabSave.isEnabled = true
                    Snackbar.make(
                        binding.root,
                        getString(R.string.transaction_saved),
                        Snackbar.LENGTH_LONG
                    ).show()
                    findNavController().navigateUp()
                }
                is AddTransactionViewModel.AddTransactionUiState.Error -> {
                    binding.fabSave.isEnabled = true
                    Snackbar.make(
                        binding.root,
                        state.message,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                is AddTransactionViewModel.AddTransactionUiState.Idle -> {
                    binding.fabSave.isEnabled = true
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 