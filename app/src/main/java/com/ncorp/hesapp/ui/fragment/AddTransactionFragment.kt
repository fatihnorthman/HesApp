package com.ncorp.hesapp.ui.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ncorp.hesapp.R
import com.ncorp.hesapp.data.model.Contact
import com.ncorp.hesapp.data.model.Product
import com.ncorp.hesapp.data.model.TransactionType
import com.ncorp.hesapp.databinding.FragmentAddTransactionBinding
import com.ncorp.hesapp.ui.viewmodel.AddTransactionViewModel
import com.ncorp.hesapp.ui.viewmodel.ContactsViewModel
import com.ncorp.hesapp.ui.viewmodel.ProductsViewModel
import com.ncorp.hesapp.utils.SoundUtils
import com.ncorp.hesapp.utils.ToastUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddTransactionFragment : Fragment() {

    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddTransactionViewModel by viewModels()
    private val contactsViewModel: ContactsViewModel by viewModels()
    private val productsViewModel: ProductsViewModel by viewModels()
    private val args: AddTransactionFragmentArgs by navArgs()

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private var selectedDate: Date = Date()
    private var selectedContact: Contact? = null
    private var contactsList: List<Contact> = emptyList()
    private var productsList: List<Product> = emptyList()
    private var selectedProduct: Product? = null

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
        
        val slideUpAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up)
        view.startAnimation(slideUpAnimation)
        
        setupViews()
        setupListeners()
        observeViewModel()
        observeContacts()
        observeProducts()

        // Dashboard'dan satış/alış hızlı aksiyonu ile gelindiyse ön seçim yap
        handlePreselectedFlow()
    }

    private fun setupViews() {
        binding.etDate.setText(dateFormat.format(selectedDate))
    }

    private fun setupListeners() {
        // İşlem türü chip'leri
        binding.chipIncome.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                SoundUtils.playButtonClick()
                updateContactFieldVisibility(R.id.chipIncome)
                syncProductSectionWithFlow()
            }
        }
        binding.chipExpense.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                SoundUtils.playButtonClick()
                updateContactFieldVisibility(R.id.chipExpense)
                syncProductSectionWithFlow()
            }
        }
        binding.chipDebt.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                SoundUtils.playButtonClick()
                updateContactFieldVisibility(R.id.chipDebt)
                syncProductSectionWithFlow()
            }
        }
        binding.chipReceivable.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                SoundUtils.playButtonClick()
                updateContactFieldVisibility(R.id.chipReceivable)
                syncProductSectionWithFlow()
            }
        }

        // Ürün akışı chip'leri (satış/alış)
        binding.chipSale.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                SoundUtils.playButtonClick()
                binding.tilProduct.visibility = View.VISIBLE
                binding.tilQuantity.visibility = View.VISIBLE
                binding.chipIncome.isChecked = true // Satış => Gelir
                // İlk seçimde ürün yoksa diyalogu aç
                if (selectedProduct == null && productsList.isNotEmpty()) {
                    showProductPickerDialog()
                }
            } else {
                // Satış kapandıysa ve alış da kapalıysa ürün alanını gizle/temizle
                if (!binding.chipPurchase.isChecked) {
                    clearProductSelection()
                    syncProductSectionWithFlow()
                }
            }
        }
        binding.chipPurchase.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                SoundUtils.playButtonClick()
                binding.tilProduct.visibility = View.VISIBLE
                binding.tilQuantity.visibility = View.VISIBLE
                binding.chipExpense.isChecked = true // Alış => Gider
                // İlk seçimde ürün yoksa diyalogu aç
                if (selectedProduct == null && productsList.isNotEmpty()) {
                    showProductPickerDialog()
                }
            } else {
                // Alış kapandıysa ve satış da kapalıysa ürün alanını gizle/temizle
                if (!binding.chipSale.isChecked) {
                    clearProductSelection()
                    syncProductSectionWithFlow()
                }
            }
        }

        binding.etDate.setOnClickListener {
            SoundUtils.playButtonClick()
            showDatePicker()
        }

        binding.etContact.setOnClickListener {
            SoundUtils.playButtonClick()
            showContactPickerDialog()
        }

        binding.etProduct.setOnClickListener {
            SoundUtils.playButtonClick()
            showProductPickerDialog()
        }

        binding.fabSave.setOnClickListener {
            val scaleAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.button_scale)
            binding.fabSave.startAnimation(scaleAnimation)
            SoundUtils.playButtonClick()
            
            saveTransaction()
        }
    }

    private fun observeContacts() {
        viewLifecycleOwner.lifecycleScope.launch {
            contactsViewModel.contacts.collectLatest { list ->
                contactsList = list
            }
        }
    }

    private fun observeProducts() {
        viewLifecycleOwner.lifecycleScope.launch {
            productsViewModel.products.collectLatest { list ->
                productsList = list
                // Eğer preselect bekliyorsak ve liste geldi ise diyalogu aç
                maybeOpenProductDialogAfterPreselect()
            }
        }
    }

    private var pendingPreselectFlow: String? = null

    private fun handlePreselectedFlow() {
        val flow = args.preselectFlow
        if (flow.isNullOrEmpty()) return
        pendingPreselectFlow = flow
        when (flow.lowercase(Locale.getDefault())) {
            "sale" -> {
                binding.chipSale.isChecked = true
                binding.tilProduct.visibility = View.VISIBLE
                binding.tilQuantity.visibility = View.VISIBLE
                binding.chipIncome.isChecked = true
            }
            "purchase" -> {
                binding.chipPurchase.isChecked = true
                binding.tilProduct.visibility = View.VISIBLE
                binding.tilQuantity.visibility = View.VISIBLE
                binding.chipExpense.isChecked = true
            }
        }
        maybeOpenProductDialogAfterPreselect()
    }

    private fun maybeOpenProductDialogAfterPreselect() {
        val flow = pendingPreselectFlow ?: return
        // Ürünler yüklendiyse diyalogu bir kez aç
        if (productsList.isNotEmpty()) {
            pendingPreselectFlow = null
            // Kısa bir gecikme ile UI stabilize olsun
            view?.post { showProductPickerDialog() }
        }
    }

    private fun syncProductSectionWithFlow() {
        val shouldShow = binding.chipSale.isChecked || binding.chipPurchase.isChecked
        binding.tilProduct.visibility = if (shouldShow) View.VISIBLE else View.GONE
        binding.tilQuantity.visibility = if (shouldShow) View.VISIBLE else View.GONE
    }

    private fun clearProductSelection() {
        selectedProduct = null
        binding.etProduct.setText("")
        binding.etQuantity.setText("")
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

    private fun showContactPickerDialog() {
        val contacts = contactsList
        if (contacts.isEmpty()) {
            ToastUtils.showCustomToast(requireContext(), "Henüz kişi eklenmemiş. Önce kişiler sayfasından kişi ekleyin.")
            return
        }
        val contactNames = contacts.map { it.name }
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.item_dialog_contact,
            R.id.tvTitle,
            contactNames
        )
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Kişi Seç")
            .setAdapter(adapter) { _, which ->
                selectedContact = contacts[which]
                binding.etContact.setText(selectedContact?.name)
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    private fun showProductPickerDialog() {
        val products = productsList
        if (products.isEmpty()) {
            ToastUtils.showCustomToast(requireContext(), "Henüz ürün eklenmemiş. Önce ürün ekleyin.")
            return
        }
        val productNames = products.map { it.name }
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.item_dialog_contact,
            R.id.tvTitle,
            productNames
        )
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Ürün Seç")
            .setAdapter(adapter) { _, which ->
                selectedProduct = products[which]
                binding.etProduct.setText(selectedProduct?.name)
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    private fun saveTransaction() {
        val description = binding.etDescription.text.toString().trim()
        val category = binding.etCategory.text.toString().trim()
        val amountText = binding.etAmount.text.toString().trim()
        val notes = binding.etNotes.text.toString().trim()
        val quantityText = binding.etQuantity.text?.toString()?.trim()

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
        val amount = amountText.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            binding.tilAmount.error = "Geçersiz tutar"
            return
        }

        val selectedChipId = binding.chipGroupTransactionType.checkedChipId
        val transactionType = when (selectedChipId) {
            R.id.chipIncome -> TransactionType.INCOME
            R.id.chipExpense -> TransactionType.EXPENSE
            R.id.chipDebt -> TransactionType.DEBT
            R.id.chipReceivable -> TransactionType.RECEIVABLE
            else -> {
                ToastUtils.showCustomToast(requireContext(), "İşlem türü seçin")
                return
            }
        }

        val isSale = binding.chipSale.isChecked
        val isPurchase = binding.chipPurchase.isChecked
        var parsedQuantity: Int? = null

        if (isSale || isPurchase) {
            if (selectedProduct == null) {
                ToastUtils.showCustomToast(requireContext(), "Ürün seçin")
                return
            }
            if (quantityText.isNullOrEmpty()) {
                binding.tilQuantity.error = "Miktar gerekli"
                return
            }
            parsedQuantity = quantityText.toIntOrNull()
            if (parsedQuantity == null || parsedQuantity <= 0) {
                binding.tilQuantity.error = "Geçersiz miktar"
                return
            }
        }

        binding.tilDescription.error = null
        binding.tilCategory.error = null
        binding.tilAmount.error = null
        binding.tilQuantity.error = null

        viewModel.addTransaction(
            type = transactionType,
            description = description,
            category = category,
            amount = amount,
            date = selectedDate,
            notes = notes.ifEmpty { null },
            contactId = selectedContact?.id,
            productId = selectedProduct?.id,
            quantity = parsedQuantity
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
        selectedContact = null
        
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
                    SoundUtils.playSuccess()
                    ToastUtils.showSuccessSnackbar(binding.root, getString(R.string.transaction_saved))
                    
                    binding.root.postDelayed({
                        findNavController().navigateUp()
                    }, 500)
                }
                is AddTransactionViewModel.AddTransactionUiState.Error -> {
                    binding.fabSave.isEnabled = true
                    SoundUtils.playError()
                    ToastUtils.showErrorSnackbar(binding.root, state.message)
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