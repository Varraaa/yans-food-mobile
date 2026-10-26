package com.example.yansfood.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yansfood.data.model.LoginResponse
import com.example.yansfood.data.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _loginResult.value = Result.failure(Exception("Email dan kata sandi wajib diisi."))
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            val result = authRepository.login(email, pass)
            _isLoading.value = false
            _loginResult.value = result
        }
    }

    fun isAlreadyLoggedIn(): Boolean = authRepository.isLoggedIn()
}
