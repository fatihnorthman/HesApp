package com.ncorp.hesapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ncorp.hesapp.databinding.FragmentContactsBinding
import com.ncorp.hesapp.ui.adapter.ContactAdapter
import com.ncorp.hesapp.ui.viewmodel.ContactsViewModel
import com.ncorp.hesapp.utils.ToastUtils
import com.ncorp.hesapp.utils.SoundUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.appcompat.widget.SearchView

@AndroidEntryPoint
class ContactsFragment : Fragment() {
    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ContactsViewModel by viewModels()
    private var adapter: ContactAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val lite = requireContext().getSharedPreferences("settings", android.content.Context.MODE_PRIVATE)
            .getBoolean("lite_mode", false)
        if (!lite) {
            val fadeInAnimation = android.view.animation.AnimationUtils.loadAnimation(requireContext(), com.ncorp.hesapp.R.anim.fade_in)
            view.startAnimation(fadeInAnimation)
        }
        setupRecyclerView()
        setupFab()
        observeContacts()
        setupSearch()
    }

    private fun setupRecyclerView() {
        binding.recyclerContacts.layoutManager = LinearLayoutManager(requireContext())
        adapter = ContactAdapter(emptyList()) { contact ->
            showDeleteDialog(contact)
        }
        binding.recyclerContacts.adapter = adapter
    }

    private fun setupFab() {
        binding.fabAddContact.setOnClickListener {
            SoundUtils.playButtonClick()
            showAddContactDialog()
        }
    }

    // Swipe-to-delete kaldırıldı; silme sadece butonla yapılacak

    private fun observeContacts() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.contacts.collectLatest { contacts ->
                adapter = ContactAdapter(contacts) { contact ->
                    showDeleteDialog(contact)
                }
                binding.recyclerContacts.adapter = adapter
            }
        }
        
        // Hata durumlarını gözlemle
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collectLatest { error ->
                error?.let {
                    com.ncorp.hesapp.utils.ToastUtils.showErrorSnackbar(binding.root, it)
                    viewModel.clearError()
                }
            }
        }
    }

    private fun setupSearch() {
        binding.searchViewContacts.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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

    private fun showAddContactDialog() {
        val dialogView = layoutInflater.inflate(com.ncorp.hesapp.R.layout.dialog_add_contact, null)
        val etName = dialogView.findViewById<android.widget.EditText>(com.ncorp.hesapp.R.id.etContactName)
        val etPhone = dialogView.findViewById<android.widget.EditText>(com.ncorp.hesapp.R.id.etContactPhone)
        val etAddress = dialogView.findViewById<android.widget.EditText>(com.ncorp.hesapp.R.id.etContactAddress)
        val rgIdType = dialogView.findViewById<android.widget.RadioGroup>(com.ncorp.hesapp.R.id.rgIdType)
        val rbTckn = dialogView.findViewById<android.widget.RadioButton>(com.ncorp.hesapp.R.id.rbTCKN)
        val rbVkn = dialogView.findViewById<android.widget.RadioButton>(com.ncorp.hesapp.R.id.rbVKN)
        val etIdNumber = dialogView.findViewById<android.widget.EditText>(com.ncorp.hesapp.R.id.etIdNumber)
        val spinnerContactType = dialogView.findViewById<android.widget.Spinner>(com.ncorp.hesapp.R.id.spinnerContactType)
        // Kimlik tipi değişince hint ve maxLength güncelle
        fun updateIdField() {
            val isT = rbTckn.isChecked
            etIdNumber.hint = if (isT) "TCKN" else "VKN"
            etIdNumber.text?.clear()
            val filters = arrayOf(android.text.InputFilter.LengthFilter(if (isT) 11 else 10))
            etIdNumber.filters = filters
        }
        rgIdType.setOnCheckedChangeListener { _, _ -> updateIdField() }
        updateIdField()
        
        // Kişi türü spinner'ını ayarla
        val contactTypes = com.ncorp.hesapp.data.model.ContactType.values()
        val adapter = android.widget.ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            contactTypes.map { it.displayName }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerContactType.adapter = adapter
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Kişi Ekle")
            .setView(dialogView)
            .setPositiveButton("Ekle") { _, _ ->
                val name = etName.text.toString().trim()
                val phone = etPhone.text.toString().trim()
                val selectedContactType = contactTypes[spinnerContactType.selectedItemPosition]
                val address = etAddress.text.toString().trim().ifEmpty { null }
                val isTckn = rbTckn.isChecked
                val idVal = etIdNumber.text.toString().trim()
                val tckn = if (isTckn) idVal else ""
                val vkn = if (!isTckn) idVal else ""
                val valid = if (isTckn) (idVal.length == 11 && idVal.all { it.isDigit() }) else (idVal.length == 10 && idVal.all { it.isDigit() })

                if (name.isNotEmpty() && (idVal.isEmpty() || valid)) {
                    viewModel.addContact(
                        name,
                        if (phone.isNotEmpty()) phone else null,
                        selectedContactType,
                        address,
                        tckn.ifEmpty { null },
                        vkn.ifEmpty { null }
                    )
                    SoundUtils.playSuccess()
                } else {
                    SoundUtils.playError()
                    val msg = if (name.isEmpty()) "İsim boş olamaz" else if (isTckn) "TCKN 11 haneli olmalı" else "VKN 10 haneli olmalı"
                    ToastUtils.showCustomToast(requireContext(), msg)
                }
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    private fun showDeleteDialog(contact: com.ncorp.hesapp.data.model.Contact) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Kişi Sil")
            .setMessage("${contact.name} kişisini silmek istiyor musunuz?")
            .setPositiveButton("Sil") { _, _ ->
                viewModel.deleteContact(contact)
                SoundUtils.playButtonClick()
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 