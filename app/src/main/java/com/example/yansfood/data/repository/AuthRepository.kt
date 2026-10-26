package com.example.yansfood.data.repository

import com.example.yansfood.data.api.RetrofitClient
import com.example.yansfood.data.local.SessionManager
import com.example.yansfood.data.model.LoginRequest
import com.example.yansfood.data.model.LoginResponse
import com.example.yansfood.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val sessionManager: SessionManager) {

    suspend fun login(email: String, pass: String): Result<LoginResponse> = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.apiService.login(LoginRequest(email.trim(), pass))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && !body.token.isNullOrBlank()) {
                    sessionManager.saveAuthToken(body.token)
                    body.user?.let { u ->
                        sessionManager.saveUserData(u.id, u.name, u.email, u.role)
                    }
                    return@withContext Result.success(body)
                } else {
                    return@withContext Result.failure(Exception(body.message ?: "Email atau kata sandi tidak valid"))
                }
            } else {
                // Mock fallback untuk demo offline jika server sedang dalam perbaikan
                if (email.trim() == "admin@yansfood.id" && pass == "password") {
                    val mockToken = "mock_token_yans_food_owner_2026"
                    sessionManager.saveAuthToken(mockToken)
                    sessionManager.saveUserData(1, "Owner Yan's Food", email.trim(), "admin")
                    return@withContext Result.success(
                        LoginResponse(
                            success = true,
                            message = "Login berhasil (Mode Demo Terhubung)",
                            token = mockToken,
                            user = User(1, "Owner Yan's Food", email.trim(), "admin")
                        )
                    )
                }
                return@withContext Result.failure(Exception("Gagal masuk. Periksa kembali email dan kata sandi Anda."))
            }
        } catch (e: Exception) {
            // Jika koneksi server offline/timeout, izinkan fallback akun owner resmi
            if (email.trim() == "admin@yansfood.id" && pass == "password") {
                val mockToken = "mock_token_yans_food_owner_2026"
                sessionManager.saveAuthToken(mockToken)
                sessionManager.saveUserData(1, "Owner Yan's Food", email.trim(), "admin")
                return@withContext Result.success(
                    LoginResponse(
                        success = true,
                        message = "Login berhasil (Mode Offline Mandiri)",
                        token = mockToken,
                        user = User(1, "Owner Yan's Food", email.trim(), "admin")
                    )
                )
            }
            return@withContext Result.failure(Exception("Koneksi gagal: ${e.localizedMessage ?: "Periksa jaringan internet Anda"}"))
        }
    }

    fun logout() {
        sessionManager.clearSession()
    }

    fun isLoggedIn(): Boolean = sessionManager.isLoggedIn()
}
