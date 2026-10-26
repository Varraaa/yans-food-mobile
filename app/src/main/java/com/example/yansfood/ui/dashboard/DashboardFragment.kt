package com.example.yansfood.ui.dashboard

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yansfood.R
import com.example.yansfood.data.local.SessionManager
import com.example.yansfood.data.repository.DashboardRepository
import com.example.yansfood.databinding.FragmentDashboardBinding
import com.example.yansfood.util.animateCurrencyCountUp
import com.example.yansfood.util.gone
import com.example.yansfood.util.show
import com.example.yansfood.util.toRupiahFormat

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: DashboardViewModel
    private lateinit var transactionAdapter: RecentTransactionAdapter
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        val repository = DashboardRepository(sessionManager)
        viewModel = DashboardViewModel(repository)

        setupRecyclerView()
        setupSwipeRefresh()
        observeData()

        binding.tvWelcomeUser.text = "Halo, ${sessionManager.getUserName()}"

        viewModel.loadDashboardData()
    }

    private fun setupRecyclerView() {
        transactionAdapter = RecentTransactionAdapter()
        binding.rvRecentTransactions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transactionAdapter
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshDashboard.setColorSchemeColors(
            requireContext().getColor(R.color.brand_primary)
        )
        binding.swipeRefreshDashboard.setOnRefreshListener {
            viewModel.loadDashboardData()
        }
    }

    private fun triggerLightHapticFeedback() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = requireContext().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(15, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                vibrator?.vibrate(15)
            }
        } catch (_: Exception) {
            // Ignore jika izin/hardware vibrator tidak tersedia
        }
    }

    private fun observeData() {
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.swipeRefreshDashboard.isRefreshing = loading
            if (!loading) {
                triggerLightHapticFeedback()
            }
        }

        viewModel.dashboardData.observe(viewLifecycleOwner) { response ->
            response.data?.let { summary ->
                // Animasi Count-Up untuk Omset
                binding.tvKpiSales.animateCurrencyCountUp(summary.todaySales, 600L)
                binding.tvKpiTransactions.text = "${summary.completedTransactions} Struk Selesai"
                binding.tvKpiExpenses.text = summary.kitchenExpenses.toRupiahFormat()
                binding.tvKpiProfit.text = summary.netProfit.toRupiahFormat()

                // Peringatan Stok
                if (summary.lowStockCount > 0 && !summary.lowStockProductName.isNullOrBlank()) {
                    binding.cardStockAlert.show()
                    binding.tvStockAlertMessage.text = "${summary.lowStockProductName} tersisa ${summary.lowStockRemaining} porsi (Segera restock)."
                } else {
                    binding.cardStockAlert.gone()
                }
            }

            // Feed Transaksi
            val transactions = response.recentTransactions.orEmpty()
            if (transactions.isNotEmpty()) {
                binding.rvRecentTransactions.show()
                binding.tvEmptyTransactions.gone()
                transactionAdapter.updateData(transactions)
            } else {
                binding.rvRecentTransactions.gone()
                binding.tvEmptyTransactions.show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
