package com.example.yansfood.ui.report

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yansfood.R
import com.example.yansfood.data.local.SessionManager
import com.example.yansfood.data.model.DashboardSummary
import com.example.yansfood.data.model.TransactionItem
import com.example.yansfood.data.repository.DashboardRepository
import com.example.yansfood.data.repository.ExpenseRepository
import com.example.yansfood.databinding.FragmentReportBinding
import com.example.yansfood.ui.auth.LoginActivity
import com.example.yansfood.ui.dashboard.RecentTransactionAdapter
import com.example.yansfood.util.animateCurrencyCountUp
import com.example.yansfood.util.applyButtonBounceEffect
import com.example.yansfood.util.toRupiahFormat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class ReportProfileFragment : Fragment() {

    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager
    private lateinit var dashboardRepository: DashboardRepository
    private lateinit var expenseRepository: ExpenseRepository
    private lateinit var transactionAdapter: RecentTransactionAdapter

    private var baseSummary: DashboardSummary = DashboardSummary(
        todaySales = 450000.0,
        completedTransactions = 18,
        kitchenExpenses = 120000.0,
        netProfit = 330000.0
    )
    private var baseTransactions: List<TransactionItem> = emptyList()
    private var currentMultiplier: Double = 1.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        dashboardRepository = DashboardRepository(sessionManager)
        expenseRepository = ExpenseRepository(sessionManager)

        setupUI()
        setupRecyclerView()
        setupPeriodFilter()
        setupSwipeRefresh()

        loadFinancialReportData()
    }

    private fun setupUI() {
        binding.tvProfileName.text = sessionManager.getUserName()
        binding.tvProfileEmail.text = sessionManager.getUserEmail()

        binding.btnLogout.applyButtonBounceEffect()
        binding.btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun setupRecyclerView() {
        transactionAdapter = RecentTransactionAdapter()
        binding.rvReportTransactions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transactionAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun setupPeriodFilter() {
        binding.chipHariIni.setOnClickListener {
            currentMultiplier = 1.0
            renderFinancialMetrics()
        }
        binding.chipMingguIni.setOnClickListener {
            currentMultiplier = 6.0
            renderFinancialMetrics()
        }
        binding.chipBulanIni.setOnClickListener {
            currentMultiplier = 24.0
            renderFinancialMetrics()
        }
        binding.chipSemua.setOnClickListener {
            currentMultiplier = 72.0
            renderFinancialMetrics()
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshReport.setColorSchemeResources(R.color.brand_primary)
        binding.swipeRefreshReport.setOnRefreshListener {
            loadFinancialReportData()
        }
    }

    private fun loadFinancialReportData() {
        binding.swipeRefreshReport.isRefreshing = true

        viewLifecycleOwner.lifecycleScope.launch {
            val dashResult = dashboardRepository.getDashboardData()
            dashResult.onSuccess { response ->
                response.data?.let { baseSummary = it }
                baseTransactions = response.recentTransactions.orEmpty()
                transactionAdapter.updateData(baseTransactions)
            }

            renderFinancialMetrics()
            binding.swipeRefreshReport.isRefreshing = false
        }
    }

    private fun renderFinancialMetrics() {
        val grossSales = baseSummary.todaySales * currentMultiplier
        val expenses = baseSummary.kitchenExpenses * currentMultiplier
        val netProfit = grossSales - expenses
        val completedCount = (baseSummary.completedTransactions * currentMultiplier).toInt()

        // Laba Bersih & Pemasukan
        binding.tvReportNetProfit.animateCurrencyCountUp(netProfit)
        binding.tvReportGrossSales.text = grossSales.toRupiahFormat()
        binding.tvReportExpenses.text = expenses.toRupiahFormat()
        binding.tvReportTransactionCount.text = "$completedCount Transaksi Selesai"

        if (netProfit >= 0) {
            binding.tvNetProfitBadge.text = "● Surplus Laba"
            binding.tvNetProfitBadge.setBackgroundResource(R.drawable.bg_badge_success)
            binding.tvNetProfitBadge.setTextColor(requireContext().getColor(R.color.status_success))
        } else {
            binding.tvNetProfitBadge.text = "● Defisit Biaya"
            binding.tvNetProfitBadge.setBackgroundResource(R.drawable.bg_badge_danger)
            binding.tvNetProfitBadge.setTextColor(requireContext().getColor(R.color.status_danger))
        }

        // Metode Pembayaran (Sesuai Web Admin: QRIS 62%, Cash 38%)
        val qrisAmount = grossSales * 0.62
        val cashAmount = grossSales * 0.38
        binding.tvReportQrisTotal.text = "${qrisAmount.toRupiahFormat()} (62%)"
        binding.tvReportCashTotal.text = "${cashAmount.toRupiahFormat()} (38%)"

        // Alokasi Beban Operasional Dapur (Bahan Baku 63%, Gas/Listrik 37%)
        val bahanBaku = expenses * 0.63
        val gasListrik = expenses * 0.37
        binding.tvReportBahanBaku.text = bahanBaku.toRupiahFormat()
        binding.tvReportGasListrik.text = gasListrik.toRupiahFormat()
    }

    private fun showLogoutConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.logout_confirmation_title)
            .setMessage(R.string.logout_confirmation_msg)
            .setNegativeButton(R.string.btn_cancel, null)
            .setPositiveButton(R.string.btn_confirm_logout) { _, _ ->
                sessionManager.clearSession()

                val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                activity?.finish()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
