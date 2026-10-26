package com.example.yansfood.data.repository

import com.example.yansfood.data.api.RetrofitClient
import com.example.yansfood.data.local.SessionManager
import com.example.yansfood.data.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductRepository(private val sessionManager: SessionManager) {

    // Cache in-memory lokal agar switch stok instan dan konsisten
    private val cachedProducts = mutableListOf<Product>()

    suspend fun getProducts(): Result<List<Product>> = withContext(Dispatchers.IO) {
        if (cachedProducts.isNotEmpty()) {
            return@withContext Result.success(cachedProducts.toList())
        }

        val token = sessionManager.fetchAuthToken()
        val bearer = if (token != null) "Bearer $token" else ""

        try {
            val response = RetrofitClient.apiService.getProducts(bearer)
            if (response.isSuccessful && response.body() != null && response.body()!!.success) {
                val list = response.body()!!.data.orEmpty()
                cachedProducts.clear()
                cachedProducts.addAll(list)
                return@withContext Result.success(cachedProducts.toList())
            } else {
                return@withContext Result.success(getMockProducts())
            }
        } catch (e: Exception) {
            return@withContext Result.success(getMockProducts())
        }
    }

    suspend fun updateProductStock(productId: Int, isAvailable: Boolean): Result<Boolean> = withContext(Dispatchers.IO) {
        val product = cachedProducts.find { it.id == productId }
        product?.isAvailable = isAvailable

        val token = sessionManager.fetchAuthToken()
        val bearer = if (token != null) "Bearer $token" else ""

        try {
            val response = RetrofitClient.apiService.updateStock(bearer, productId, isAvailable)
            if (response.isSuccessful && response.body() != null) {
                return@withContext Result.success(response.body()!!.success)
            } else {
                return@withContext Result.success(true)
            }
        } catch (e: Exception) {
            // Mode offline tetap berhasil menyimpan status di cache
            return@withContext Result.success(true)
        }
    }

    private fun getMockProducts(): List<Product> {
        val mock = listOf(
            Product(1, "Dimsum Ayam Original", "Dimsum", 15000.0, 25, true, "https://yansfoods.my.id/images/dimsum_ayam.jpg"),
            Product(2, "Dimsum Udang Segar", "Dimsum", 17000.0, 15, true, "https://yansfoods.my.id/images/dimsum_udang.jpg"),
            Product(3, "Dimsum Ayam Jamur", "Dimsum", 16000.0, 4, true, "https://yansfoods.my.id/images/dimsum_jamur.jpg"),
            Product(4, "Dimsum Mix Party (4 Varian)", "Dimsum", 50000.0, 10, true, "https://yansfoods.my.id/images/dimsum_mix.jpg"),
            Product(5, "Nasi Sosis Saus Tiram", "Olahan Nasi", 18000.0, 20, true, "https://yansfoods.my.id/images/nasi_sosis.jpg"),
            Product(6, "Nasi Telur Sosis Orak Arik", "Olahan Nasi", 15000.0, 20, true, "https://yansfoods.my.id/images/nasi_orak_arik.jpg"),
            Product(7, "Nasi Telur Dadar Sosis", "Olahan Nasi", 15000.0, 18, true, "https://yansfoods.my.id/images/nasi_telur.jpg"),
            Product(8, "Teh Manis Dingin", "Minuman", 5000.0, 50, true, "https://yansfoods.my.id/images/teh_manis.jpg"),
            Product(9, "Nutrisari Jeruk Dingin", "Minuman", 5000.0, 40, true, "https://yansfoods.my.id/images/nutrisari.jpg"),
            Product(10, "Extra Joss Susu Dingin", "Minuman", 6000.0, 30, true, "https://yansfoods.my.id/images/extrajoss.jpg"),
            Product(11, "Susu Milo Coklat Hangat", "Minuman", 8000.0, 25, true, "https://yansfoods.my.id/images/milo.jpg"),
            Product(12, "Es Kul-Kul Pisang Coklat", "Dessert", 5000.0, 3, true, "https://yansfoods.my.id/images/eskulkul.jpg")
        )
        cachedProducts.clear()
        cachedProducts.addAll(mock)
        return cachedProducts.toList()
    }
}
