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

        private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale("tr"))
        private val currencyFormat = java.text.NumberFormat.getCurrencyInstance(Locale("tr", "TR"))

        fun bind(transaction: Transaction) {
            binding.apply {
                // İşlem açıklaması
                tvDescription.text = transaction.description

                // Kategori
                tvCategory.text = transaction.category

                // Tarih
                tvDate.text = dateFormat.format(transaction.date)

                // Tutar
                tvAmount.text = currencyFormat.format(transaction.amount)

                // İşlem türü
                tvType.text = transaction.type.getDisplayName()

                // İşlem türüne göre renklendirme
                val gradientRes = transaction.type.getGradientRes()
                ivTransactionType.setImageResource(gradientRes)

                // Tutar rengi
                val amountColor = when (transaction.type) {
                    TransactionType.INCOME -> root.context.getColor(android.R.color.holo_green_dark)
                    TransactionType.EXPENSE -> root.context.getColor(android.R.color.holo_red_dark)
                    TransactionType.DEBT -> root.context.getColor(android.R.color.holo_orange_dark)
                    TransactionType.RECEIVABLE -> root.context.getColor(android.R.color.holo_blue_dark)
                }
                tvAmount.setTextColor(amountColor)
                tvType.setTextColor(amountColor)

                // Click listener'lar
                root.setOnClickListener {
                    onItemClick(transaction)
                }

                root.setOnLongClickListener {
                    onItemLongClick(transaction)
                }
            }
        }
    }

    /**
     * DiffUtil Callback
     * 
     * İşlem listesindeki değişiklikleri tespit eder.
     */
    private class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
} 