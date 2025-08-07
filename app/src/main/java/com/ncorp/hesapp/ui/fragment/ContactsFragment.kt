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
        setupRecyclerView()
        setupFab()
        observeContacts()
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

    private fun observeContacts() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.contacts.collectLatest { contacts ->
                adapter = ContactAdapter(contacts) { contact ->
                    showDeleteDialog(contact)
                }
                binding.recyclerContacts.adapter = adapter
            }
        }
    }

    private fun showAddContactDialog() {
        val dialogView = layoutInflater.inflate(com.ncorp.hesapp.R.layout.dialog_add_contact, null)
        val etName = dialogView.findViewById<android.widget.EditText>(com.ncorp.hesapp.R.id.etContactName)
        val etPhone = dialogView.findViewById<android.widget.EditText>(com.ncorp.hesapp.R.id.etContactPhone)
        val spinnerContactType = dialogView.findViewById<android.widget.Spinner>(com.ncorp.hesapp.R.id.spinnerContactType)
        
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
                
                if (name.isNotEmpty()) {
                    viewModel.addContact(name, if (phone.isNotEmpty()) phone else null, selectedContactType)
                    SoundUtils.playSuccess()
                } else {
                    SoundUtils.playError()
                    ToastUtils.showCustomToast(requireContext(), "İsim boş olamaz")
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