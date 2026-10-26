package com.example.yansfood.ui.expense

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.yansfood.R
import com.example.yansfood.data.local.SessionManager
import com.example.yansfood.data.repository.ExpenseRepository
import com.example.yansfood.databinding.ActivityAddExpenseBinding
import com.example.yansfood.util.applyButtonBounceEffect
import com.example.yansfood.util.gone
import com.example.yansfood.util.show
import com.google.android.material.snackbar.Snackbar

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddExpenseBinding
    private lateinit var viewModel: ExpenseViewModel
    private var capturedBitmap: Bitmap? = null

    // Peluncur Kamera Bawaan HP
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            capturedBitmap = bitmap
            binding.ivReceiptPreview.setImageBitmap(bitmap)
            binding.cardReceiptPreview.show()
            binding.btnCaptureReceipt.text = "📷 Foto Nota Tersimpan (Klik untuk ganti)"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sessionManager = SessionManager(this)
        val repository = ExpenseRepository(sessionManager)
        viewModel = ExpenseViewModel(repository)

        setupToolbar()
        setupCategoryDropdown()
        setupActions()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbarAddExpense.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupCategoryDropdown() {
        val categories = arrayOf(
            "Bahan Baku",
            "Kemasan & Packaging",
            "Gas LPG & Listrik",
            "Bumbu Dapur",
            "Transportasi Pasar",
            "Kebersihan & Cuci",
            "Gaji / Upah Harian",
            "Promosi & Brosur",
            "Lain-lain"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        binding.actvCategory.setAdapter(adapter)
    }

    private fun setupActions() {
        binding.btnCaptureReceipt.applyButtonBounceEffect()
        binding.btnCaptureReceipt.setOnClickListener {
            takePictureLauncher.launch(null)
        }

        binding.btnSaveExpense.applyButtonBounceEffect()
        binding.btnSaveExpense.setOnClickListener {
            val title = binding.etExpenseTitle.text?.toString().orEmpty()
            val category = binding.actvCategory.text.toString()
            val amountText = binding.etExpenseAmount.text?.toString().orEmpty()
            val paymentMethod = if (binding.rbCash.isChecked) "TUNAI" else "TRANSFER"
            val receiptPath = if (capturedBitmap != null) "nota_captured_local.jpg" else null

            viewModel.submitExpense(title, category, amountText, paymentMethod, receiptPath)
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { loading ->
            if (loading) {
                binding.btnSaveExpense.isEnabled = false
                binding.progressBarSave.show()
            } else {
                binding.btnSaveExpense.isEnabled = true
                binding.progressBarSave.gone()
            }
        }

        viewModel.addExpenseResult.observe(this) { result ->
            result.onSuccess {
                finish()
            }.onFailure { error ->
                val snackbar = Snackbar.make(binding.layoutAddExpenseRoot, error.localizedMessage ?: "Gagal menyimpan pengeluaran", Snackbar.LENGTH_LONG)
                snackbar.setBackgroundTint(getColor(R.color.status_danger))
                snackbar.show()
            }
        }
    }
}
