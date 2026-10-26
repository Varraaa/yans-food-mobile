package com.example.yansfood.ui.expense

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yansfood.R
import com.example.yansfood.data.local.SessionManager
import com.example.yansfood.data.repository.ExpenseRepository
import com.example.yansfood.databinding.FragmentExpenseBinding
import com.example.yansfood.util.applyButtonBounceEffect
import com.example.yansfood.util.gone
import com.example.yansfood.util.show

class ExpenseFragment : Fragment() {

    private var _binding: FragmentExpenseBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ExpenseViewModel
    private lateinit var adapter: ExpenseAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionManager = SessionManager(requireContext())
        val repository = ExpenseRepository(sessionManager)
        viewModel = ExpenseViewModel(repository)

        setupRecyclerView()
        setupSwipeRefresh()
        setupFab()
        observeData()

        viewModel.loadExpenses()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadExpenses()
    }

    private fun setupRecyclerView() {
        adapter = ExpenseAdapter()
        binding.rvExpenses.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@ExpenseFragment.adapter
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshExpenses.setColorSchemeColors(
            requireContext().getColor(R.color.brand_primary)
        )
        binding.swipeRefreshExpenses.setOnRefreshListener {
            viewModel.loadExpenses()
        }
    }

    private fun setupFab() {
        binding.fabAddExpense.applyButtonBounceEffect()
        binding.fabAddExpense.setOnClickListener {
            val intent = Intent(requireContext(), AddExpenseActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeData() {
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.swipeRefreshExpenses.isRefreshing = loading
        }

        viewModel.expenseList.observe(viewLifecycleOwner) { list ->
            adapter.updateData(list)
            if (list.isEmpty()) {
                binding.tvEmptyExpenses.show()
            } else {
                binding.tvEmptyExpenses.gone()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
