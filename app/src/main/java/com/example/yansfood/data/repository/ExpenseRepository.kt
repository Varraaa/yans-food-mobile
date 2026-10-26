package com.example.yansfood.data.repository

import com.example.yansfood.data.api.RetrofitClient
import com.example.yansfood.data.local.SessionManager
import com.example.yansfood.data.model.Expense
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExpenseRepository(private val sessionManager: SessionManager) {

    companion object {
        // Shared in-memory list across activities and fragments
        private val cachedExpenses = mutableListOf<Expense>()
    }

    suspend fun getExpenses(): Result<List<Expense>> = withContext(Dispatchers.IO) {
        if (cachedExpenses.isNotEmpty()) {
            return@withContext Result.success(cachedExpenses.toList())
        }

        val token = sessionManager.fetchAuthToken()
        val bearer = if (token != null) "Bearer $token" else ""

        try {
            val response = RetrofitClient.apiService.getExpenses(bearer)
            if (response.isSuccessful && response.body() != null && response.body()!!.success) {
                cachedExpenses.clear()
                cachedExpenses.addAll(response.body()!!.data.orEmpty())
                return@withContext Result.success(cachedExpenses.toList())
            } else {
                return@withContext Result.success(getMockExpenses())
            }
        } catch (e: Exception) {
            return@withContext Result.success(getMockExpenses())
        }
    }

    suspend fun addExpense(expense: Expense): Result<Boolean> = withContext(Dispatchers.IO) {
        cachedExpenses.add(0, expense)

        val token = sessionManager.fetchAuthToken()
        val bearer = if (token != null) "Bearer $token" else ""

        try {
            val response = RetrofitClient.apiService.addExpense(bearer, expense)
            if (response.isSuccessful && response.body() != null) {
                return@withContext Result.success(response.body()!!.success)
            } else {
                return@withContext Result.success(true)
            }
        } catch (e: Exception) {
            // Mode offline / demo mandiri tetap tersimpan di lokal
            return@withContext Result.success(true)
        }
    }

    private fun getMockExpenses(): List<Expense> {
        val mock = listOf(
            Expense(1, "Belanja Daging Ayam Fillet 5 Kg", "Bahan Baku", 175000.0, "TUNAI", "mock_receipt_1.jpg", "26 Okt 2026"),
            Expense(2, "Beli Gas LPG 3 Kg (2 Tabung)", "Gas & Listrik", 44000.0, "TUNAI", "mock_receipt_2.jpg", "26 Okt 2026"),
            Expense(3, "Mika Plastik Dimsum & Sumpit", "Kemasan", 65000.0, "TRANSFER", null, "25 Okt 2026"),
            Expense(4, "Minyak Goreng SunCo 2L & Saus Tiram", "Bumbu Dapur", 48000.0, "TUNAI", null, "25 Okt 2026")
        )
        cachedExpenses.clear()
        cachedExpenses.addAll(mock)
        return cachedExpenses.toList()
    }
}
