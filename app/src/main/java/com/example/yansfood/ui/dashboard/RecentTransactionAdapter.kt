package com.example.yansfood.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.yansfood.R
import com.example.yansfood.data.model.TransactionItem
import com.example.yansfood.databinding.ItemTransactionBinding
import com.example.yansfood.util.toRupiahFormat

class RecentTransactionAdapter(
    private var transactions: List<TransactionItem> = emptyList()
) : RecyclerView.Adapter<RecentTransactionAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(private val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TransactionItem) {
            binding.tvInvoiceAndTime.text = "${item.invoiceNumber} • ${item.time}"
            binding.tvItemsSummary.text = item.itemsDescription
            binding.tvTotalAmount.text = item.totalAmount.toRupiahFormat()
            binding.tvPaymentMethod.text = item.paymentMethod.uppercase()

            if (item.paymentMethod.equals("QRIS", ignoreCase = true)) {
                binding.tvPaymentMethod.setBackgroundResource(R.drawable.bg_badge_warning)
                binding.tvPaymentMethod.setTextColor(binding.root.context.getColor(R.color.status_warning))
            } else {
                binding.tvPaymentMethod.setBackgroundResource(R.drawable.bg_badge_success)
                binding.tvPaymentMethod.setTextColor(binding.root.context.getColor(R.color.status_success))
            }
        }
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
        holder.bind(transactions[position])
    }

    override fun getItemCount(): Int = transactions.size

    fun updateData(newTransactions: List<TransactionItem>) {
        this.transactions = newTransactions
        notifyDataSetChanged()
    }
}
