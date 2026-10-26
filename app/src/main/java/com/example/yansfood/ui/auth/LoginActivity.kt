package com.example.yansfood.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.yansfood.R
import com.example.yansfood.data.local.SessionManager
import com.example.yansfood.data.repository.AuthRepository
import com.example.yansfood.databinding.ActivityLoginBinding
import com.example.yansfood.ui.main.MainActivity
import com.example.yansfood.util.applyButtonBounceEffect
import com.example.yansfood.util.gone
import com.example.yansfood.util.show
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager(this)
        val authRepository = AuthRepository(sessionManager)
        viewModel = LoginViewModel(authRepository)

        // AUTO-LOGIN: Jika token sudah tersimpan, langsung melompat ke MainActivity
        if (viewModel.isAlreadyLoggedIn()) {
            navigateToMain()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        // Terapkan micro-interaction sentuhan membal (Button Bounce)
        binding.btnLogin.applyButtonBounceEffect()

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text?.toString().orEmpty()
            val password = binding.etPassword.text?.toString().orEmpty()
            viewModel.login(email, password)
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { loading ->
            if (loading) {
                binding.btnLogin.isEnabled = false
                binding.btnLogin.text = getString(R.string.login_loading)
                binding.progressBar.show()
            } else {
                binding.btnLogin.isEnabled = true
                binding.btnLogin.text = getString(R.string.btn_login)
                binding.progressBar.gone()
            }
        }

        viewModel.loginResult.observe(this) { result ->
            result.onSuccess {
                navigateToMain()
            }.onFailure { exception ->
                showErrorMessage(exception.localizedMessage ?: "Terjadi kesalahan saat masuk")
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun showErrorMessage(message: String) {
        val snackbar = Snackbar.make(binding.layoutLoginRoot, message, Snackbar.LENGTH_LONG)
        snackbar.setBackgroundTint(getColor(R.color.status_danger))
        snackbar.setTextColor(getColor(R.color.text_on_primary))
        snackbar.show()
    }
}
