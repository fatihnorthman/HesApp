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
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ncorp.hesapp.R
import com.ncorp.hesapp.data.model.Transaction
import com.ncorp.hesapp.data.model.TransactionType
import com.ncorp.hesapp.data.model.getDisplayName
import com.ncorp.hesapp.data.model.getGradientRes
import com.ncorp.hesapp.databinding.ItemTransactionBinding
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.core.content.ContextCompat

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
 * - Silme butonu desteği
 */
class TransactionAdapter(
    private val onItemClick: (Transaction) -> Unit,
    private val onItemLongClick: (Transaction) -> Boolean,
    private val onDeleteClick: (Transaction) -> Unit
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
        
        // Item animasyonu (sadece yeni eklenen item'lar için)
        if (position == 0) {
            val animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.item_animation_from_bottom)
            holder.itemView.startAnimation(animation)
        }
    }

    inner class TransactionViewHolder(private val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(transaction: Transaction) {
            binding.apply {
                tvDescription.text = transaction.description
                tvCategory.text = transaction.category
                tvDate.text = SimpleDateFormat("dd.MM.yyyy", Locale("tr")).format(transaction.date)
                tvAmount.text = String.format("₺%.2f", transaction.amount)
                tvType.text = transaction.type.getDisplayName()
                
                // İşlem türüne göre simge ayarla
                val iconRes = when (transaction.type) {
                    TransactionType.INCOME -> R.drawable.ic_income
                    TransactionType.EXPENSE -> R.drawable.ic_expense
                    TransactionType.DEBT -> R.drawable.ic_debt
                    TransactionType.RECEIVABLE -> R.drawable.ic_receivable
                    TransactionType.DEBT_PAYMENT -> R.drawable.ic_debt
                    TransactionType.RECEIVABLE_COLLECTION -> R.drawable.ic_receivable
                }
                ivTransactionType.setImageResource(iconRes)
                
                // İşlem türüne göre renk ayarla
                val colorRes = when (transaction.type) {
                    TransactionType.INCOME -> R.color.success
                    TransactionType.EXPENSE -> R.color.error
                    TransactionType.DEBT -> R.color.error
                    TransactionType.RECEIVABLE -> R.color.success
                    TransactionType.DEBT_PAYMENT -> R.color.error
                    TransactionType.RECEIVABLE_COLLECTION -> R.color.success
                }
                tvAmount.setTextColor(ContextCompat.getColor(itemView.context, colorRes))
                
                // Silme butonu
                btnDelete.setOnClickListener { onDeleteClick(transaction) }
                
                // Item tıklama
                root.setOnClickListener { onItemClick(transaction) }
                root.setOnLongClickListener { onItemLongClick(transaction) }
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