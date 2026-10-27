package com.example.yansfood.ui.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.yansfood.R
import com.example.yansfood.databinding.ActivityMainBinding
import com.example.yansfood.ui.dashboard.DashboardFragment
import com.example.yansfood.ui.expense.ExpenseFragment
import com.example.yansfood.ui.menu.MenuFragment
import com.example.yansfood.ui.report.ReportProfileFragment
import com.example.yansfood.util.applyButtonBounceEffect

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.mainLayoutRoot) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setupTopBar()
        setupBottomNavigation()

        if (savedInstanceState == null) {
            // Tampilkan fragment default (Dashboard)
            loadFragment(DashboardFragment(), "Beranda")
        }
    }

    private fun setupTopBar() {
        // Klik avatar profil untuk langsung lompat ke tab Laporan / Profil
        binding.layoutAvatarOwner.applyButtonBounceEffect()
        binding.layoutAvatarOwner.setOnClickListener {
            binding.bottomNavigation.selectedItemId = R.id.nav_report
        }

        // Klik pill status buka untuk info status gerai
        binding.layoutStatusBuka.applyButtonBounceEffect()
        binding.layoutStatusBuka.setOnClickListener {
            Toast.makeText(this, "Status Toko: Buka & Menerima Pesanan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    loadFragment(DashboardFragment(), "Beranda")
                    true
                }
                R.id.nav_menu -> {
                    loadFragment(MenuFragment(), "Menu")
                    true
                }
                R.id.nav_expense -> {
                    loadFragment(ExpenseFragment(), "Biaya")
                    true
                }
                R.id.nav_report -> {
                    loadFragment(ReportProfileFragment(), "Laporan")
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment, titleSubtitle: String) {
        binding.tvTopSubtitle.text = titleSubtitle

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
