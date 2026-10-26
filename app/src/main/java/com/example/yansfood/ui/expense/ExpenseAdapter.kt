package com.example.yansfood.ui.expense

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.yansfood.data.model.Expense
import com.example.yansfood.databinding.ItemExpenseBinding
import com.example.yansfood.util.gone
import com.example.yansfood.util.show
import com.example.yansfood.util.toRupiahFormat

class ExpenseAdapter(
    private var expenseList: List<Expense> = emptyList()
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    inner class ExpenseViewHolder(private val binding: ItemExpenseBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(expense: Expense) {
            binding.tvExpenseTitle.text = expense.title
            binding.tvExpenseAmount.text = "- ${expense.amount.toRupiahFormat()}"
            binding.tvCategoryAndDate.text = "${expense.category} • ${expense.date} • ${expense.paymentMethod.uppercase()}"

            if (!expense.receiptImageUrl.isNullOrBlank()) {
                binding.tvReceiptBadge.show()
            } else {
                binding.tvReceiptBadge.gone()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(expenseList[position])
    }

    override fun getItemCount(): Int = expenseList.size

    fun updateData(newExpenses: List<Expense>) {
        this.expenseList = newExpenses
        notifyDataSetChanged()
    }
}
