package com.example.yansfood.ui.expense

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yansfood.data.model.Expense
import com.example.yansfood.data.repository.ExpenseRepository
import kotlinx.coroutines.launch

class ExpenseViewModel(private val repository: ExpenseRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _expenseList = MutableLiveData<List<Expense>>()
    val expenseList: LiveData<List<Expense>> = _expenseList

    private val _addExpenseResult = MutableLiveData<Result<Boolean>>()
    val addExpenseResult: LiveData<Result<Boolean>> = _addExpenseResult

    fun loadExpenses() {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.getExpenses()
            _isLoading.value = false
            result.onSuccess { list ->
                _expenseList.value = list
            }
        }
    }

    fun submitExpense(
        title: String,
        category: String,
        amountText: String,
        paymentMethod: String,
        receiptPath: String?
    ) {
        if (title.isBlank()) {
            _addExpenseResult.value = Result.failure(Exception("Nama pengeluaran wajib diisi"))
            return
        }

        val amount = amountText.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _addExpenseResult.value = Result.failure(Exception("Nominal pengeluaran tidak valid"))
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            val newExpense = Expense(
                id = (System.currentTimeMillis() % 100000).toInt(),
                title = title.trim(),
                category = category,
                amount = amount,
                paymentMethod = paymentMethod,
                receiptImageUrl = receiptPath,
                date = "Hari Ini"
            )

            val result = repository.addExpense(newExpense)
            _isLoading.value = false
            _addExpenseResult.value = result
        }
    }
}
