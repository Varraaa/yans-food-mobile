package com.example.yansfood.data.api

import com.example.yansfood.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("api/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @GET("api/dashboard")
    suspend fun getDashboardSummary(
        @Header("Authorization") bearerToken: String
    ): Response<DashboardResponse>

    @GET("api/products")
    suspend fun getProducts(
        @Header("Authorization") bearerToken: String
    ): Response<ProductListResponse>

    @PUT("api/products/{id}/stock")
    suspend fun updateStock(
        @Header("Authorization") bearerToken: String,
        @Path("id") productId: Int,
        @Query("is_available") isAvailable: Boolean
    ): Response<StockUpdateResponse>

    @GET("api/expenses")
    suspend fun getExpenses(
        @Header("Authorization") bearerToken: String
    ): Response<ExpenseListResponse>

    @POST("api/expenses")
    suspend fun addExpense(
        @Header("Authorization") bearerToken: String,
        @Body expense: Expense
    ): Response<BaseResponse>
}
