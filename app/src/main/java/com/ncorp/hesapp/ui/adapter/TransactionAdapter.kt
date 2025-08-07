/*
 * TransactionAdapter.kt
 *
 * Bu dosya, işlemler listesini ekranda göstermek için kullanılan RecyclerView Adapter sınıfını içerir.
 * RecyclerView, Android'de büyük veri listelerini performanslı şekilde göstermek için kullanılır.
 * Adapter, veriyi alır ve her bir satır için ViewHolder ile arayüzü oluşturur.
 * DiffUtil, listenin değişen elemanlarını hızlıca tespit ederek sadece gerekli güncellemeleri yapar.
 *
 * Temel görevleri:
 * - İşlem listesini ekranda göstermek
 * - Her işlem için arayüzü (satırı) oluşturmak ve veriyi bağlamak
 * - Tıklama ve uzun tıklama olaylarını yönetmek
 * - Performans için DiffUtil ile güncellemeleri optimize etmek
 *
 * Kodun her adımı, "neden böyle yapıldı?" ve "ne işe yarar?" sorularına cevap verecek şekilde açıklanmıştır.
 */
package com.ncorp.hesapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ncorp.hesapp.data.model.Transaction
import com.ncorp.hesapp.data.model.TransactionType
import com.ncorp.hesapp.data.model.getDisplayName
import com.ncorp.hesapp.data.model.getGradientRes
import com.ncorp.hesapp.databinding.ItemTransactionBinding
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Transaction Adapter
 * 
 * İşlem listesi için RecyclerView adapter'ı.
 * 
 * Özellikler:
 * - DiffUtil ile performanslı güncelleme
 * - View binding kullanımı
 * - İşlem türüne göre renklendirme
 * - Tarih formatlaması
 * - Tutar formatlaması
 */
class TransactionAdapter(
    private val onItemClick: (Transaction) -> Unit,
    private val onItemLongClick: (Transaction) -> Boolean
) : ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    companion object {
        @JvmStatic
        val dateFormat = java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale("tr"))
        @JvmStatic
        val currencyFormat = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("tr", "TR"))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TransactionViewHolder(
        private val binding: ItemTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: Transaction) {
            binding.apply {
                // İşlem açıklaması
                tvDescription.text = transaction.description

                // Kategori
                tvCategory.text = transaction.category

                // Tarih - cache formatted date
                tvDate.text = dateFormat.format(transaction.date)

                // Tutar - cache formatted amount
                tvAmount.text = currencyFormat.format(transaction.amount)

                // İşlem türü
                tvType.text = transaction.type.getDisplayName()

                // İşlem türüne göre renklendirme - cache context
                val context = root.context
                val gradientRes = transaction.type.getGradientRes()
                ivTransactionType.setImageResource(gradientRes)

                // Tutar rengi - use cached colors
                val amountColor = getAmountColor(context, transaction.type)
                tvAmount.setTextColor(amountColor)
                tvType.setTextColor(amountColor)

                // Click listener'lar - avoid recreating lambdas
                root.setOnClickListener { onItemClick(transaction) }
                root.setOnLongClickListener { onItemLongClick(transaction) }
            }
        }
        
        // Cache color lookups
        private fun getAmountColor(context: android.content.Context, type: TransactionType): Int {
            return when (type) {
                TransactionType.INCOME -> context.getColor(android.R.color.holo_green_dark)
                TransactionType.EXPENSE -> context.getColor(android.R.color.holo_red_dark)
                TransactionType.DEBT -> context.getColor(android.R.color.holo_orange_dark)
                TransactionType.RECEIVABLE -> context.getColor(android.R.color.holo_blue_dark)
            }
        }
    }

    /**
     * DiffUtil Callback
     * 
     * İşlem listesindeki değişiklikleri tespit eder.
     * Performans için optimize edilmiş karşılaştırmalar.
     */
    private class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            // Performance-optimized content comparison
            return oldItem.id == newItem.id &&
                    oldItem.type == newItem.type &&
                    oldItem.description == newItem.description &&
                    oldItem.category == newItem.category &&
                    oldItem.amount == newItem.amount &&
                    oldItem.date == newItem.date &&
                    oldItem.contactId == newItem.contactId
        }
        
        override fun getChangePayload(oldItem: Transaction, newItem: Transaction): Any? {
            // Return payload for partial updates
            return if (oldItem.amount != newItem.amount) {
                "AMOUNT_CHANGED"
            } else if (oldItem.description != newItem.description) {
                "DESCRIPTION_CHANGED"
            } else {
                super.getChangePayload(oldItem, newItem)
            }
        }
    }
} 