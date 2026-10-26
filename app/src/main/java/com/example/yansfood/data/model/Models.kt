package com.example.yansfood.data.model

import com.google.gson.annotations.SerializedName

// --- AUTH MODELS ---
data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class User(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("role") val role: String? = "admin"
)

data class LoginResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("token") val token: String?,
    @SerializedName("user") val user: User?
)

// --- DASHBOARD MODELS ---
data class DashboardSummary(
    @SerializedName("today_sales") val todaySales: Double = 0.0,
    @SerializedName("completed_transactions") val completedTransactions: Int = 0,
    @SerializedName("kitchen_expenses") val kitchenExpenses: Double = 0.0,
    @SerializedName("net_profit") val netProfit: Double = 0.0,
    @SerializedName("low_stock_count") val lowStockCount: Int = 0,
    @SerializedName("low_stock_product_name") val lowStockProductName: String? = null,
    @SerializedName("low_stock_remaining") val lowStockRemaining: Int = 0
)

data class TransactionItem(
    @SerializedName("id") val id: String,
    @SerializedName("invoice_number") val invoiceNumber: String,
    @SerializedName("time") val time: String,
    @SerializedName("payment_method") val paymentMethod: String,
    @SerializedName("total_amount") val totalAmount: Double,
    @SerializedName("items_description") val itemsDescription: String
)

data class DashboardResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: DashboardSummary?,
    @SerializedName("recent_transactions") val recentTransactions: List<TransactionItem>?
)

// --- PRODUCT / MENU MODELS ---
data class Product(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("category") val category: String,
    @SerializedName("price") val price: Double,
    @SerializedName("stock") val stock: Int,
    @SerializedName("is_available") var isAvailable: Boolean = true,
    @SerializedName("image_url") val imageUrl: String? = null
)

data class ProductListResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<Product>?
)

data class StockUpdateResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("is_available") val isAvailable: Boolean
)

// --- EXPENSE MODELS ---
data class Expense(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("category") val category: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("payment_method") val paymentMethod: String,
    @SerializedName("receipt_image_url") val receiptImageUrl: String? = null,
    @SerializedName("date") val date: String
)

data class ExpenseListResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<Expense>?
)

data class BaseResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?
)
