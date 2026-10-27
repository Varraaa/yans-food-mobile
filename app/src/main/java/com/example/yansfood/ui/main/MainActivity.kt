package com.example.yansfood.ui.main

import android.graphics.Typeface
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
    private var currentTabIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.mainLayoutRoot) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Top bar diberikan padding top sebesar status bar Android (jam, sinyal, baterai)
            binding.topHeaderBar.setPadding(0, systemBars.top, 0, 0)
            // Bottom navbar diberikan padding bottom sebesar navigation bar Android (3 tombol / gesture pill)
            binding.customBottomNav.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        setupTopBar()
        setupCustomBottomNav()

        if (savedInstanceState == null) {
            // Default pilih tab 0 (Beranda)
            selectTab(0)
        }
    }

    private fun setupTopBar() {
        // Klik avatar profil untuk langsung lompat ke tab Laporan / Profil
        binding.layoutAvatarOwner.applyButtonBounceEffect()
        binding.layoutAvatarOwner.setOnClickListener {
            selectTab(3)
        }

        // Klik status "Buka" untuk feedback info gerai
        binding.layoutStatusBuka.applyButtonBounceEffect()
        binding.layoutStatusBuka.setOnClickListener {
            Toast.makeText(this, "Status Toko: Buka & Menerima Pesanan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupCustomBottomNav() {
        binding.tabNavBeranda.applyButtonBounceEffect()
        binding.tabNavMenu.applyButtonBounceEffect()
        binding.tabNavBiaya.applyButtonBounceEffect()
        binding.tabNavLaporan.applyButtonBounceEffect()

        binding.tabNavBeranda.setOnClickListener { selectTab(0) }
        binding.tabNavMenu.setOnClickListener { selectTab(1) }
        binding.tabNavBiaya.setOnClickListener { selectTab(2) }
        binding.tabNavLaporan.setOnClickListener { selectTab(3) }
    }

    fun selectTab(tabIndex: Int) {
        currentTabIndex = tabIndex

        val activeColor = ContextCompat.getColor(this, R.color.brand_primary)
        val inactiveColor = ContextCompat.getColor(this, R.color.text_secondary)

        // Reset semua tab ke status inaktif
        setTabStyle(binding.ivNavBeranda, binding.tvNavBeranda, false, inactiveColor)
        setTabStyle(binding.ivNavMenu, binding.tvNavMenu, false, inactiveColor)
        setTabStyle(binding.ivNavBiaya, binding.tvNavBiaya, false, inactiveColor)
        setTabStyle(binding.ivNavLaporan, binding.tvNavLaporan, false, inactiveColor)

        // Aktifkan tab yang dipilih
        when (tabIndex) {
            0 -> {
                setTabStyle(binding.ivNavBeranda, binding.tvNavBeranda, true, activeColor)
                binding.tvTopSubtitle.text = getString(R.string.nav_dashboard)
                loadFragment(DashboardFragment())
            }
            1 -> {
                setTabStyle(binding.ivNavMenu, binding.tvNavMenu, true, activeColor)
                binding.tvTopSubtitle.text = getString(R.string.nav_menu)
                loadFragment(MenuFragment())
            }
            2 -> {
                setTabStyle(binding.ivNavBiaya, binding.tvNavBiaya, true, activeColor)
                binding.tvTopSubtitle.text = getString(R.string.nav_expense)
                loadFragment(ExpenseFragment())
            }
            3 -> {
                setTabStyle(binding.ivNavLaporan, binding.tvNavLaporan, true, activeColor)
                binding.tvTopSubtitle.text = getString(R.string.nav_report)
                loadFragment(ReportProfileFragment())
            }
        }
    }

    private fun setTabStyle(imageView: ImageView, textView: TextView, isActive: Boolean, color: Int) {
        androidx.core.widget.ImageViewCompat.setImageTintList(
            imageView,
            android.content.res.ColorStateList.valueOf(color)
        )
        textView.setTextColor(color)
        textView.typeface = if (isActive) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
