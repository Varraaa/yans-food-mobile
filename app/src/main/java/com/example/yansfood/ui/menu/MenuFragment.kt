package com.example.yansfood.ui.menu

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yansfood.R
import com.example.yansfood.data.local.SessionManager
import com.example.yansfood.data.model.Product
import com.example.yansfood.data.repository.ProductRepository
import com.example.yansfood.databinding.FragmentMenuBinding
import com.example.yansfood.util.gone
import com.example.yansfood.util.show
import com.google.android.material.snackbar.Snackbar

class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MenuViewModel
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionManager = SessionManager(requireContext())
        val repository = ProductRepository(sessionManager)
        viewModel = MenuViewModel(repository)

        setupRecyclerView()
        setupFiltersAndSearch()
        setupSwipeRefresh()
        observeData()

        viewModel.loadProducts()
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(
            onStockToggle = { product, isAvailable ->
                handleStockToggle(product, isAvailable)
            }
        )

        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@MenuFragment.adapter
        }
    }

    private fun handleStockToggle(product: Product, isAvailable: Boolean) {
        viewModel.toggleProductStock(product, isAvailable)

        val statusText = if (isAvailable) "Tersedia" else "Habis"
        val message = "Status ${product.name} diubah menjadi $statusText."

        val snackbar = Snackbar.make(binding.layoutMenuRoot, message, Snackbar.LENGTH_LONG)
        snackbar.setAction("BATALKAN") {
            // Revert state
            val originalState = !isAvailable
            product.isAvailable = originalState
            viewModel.toggleProductStock(product, originalState)
            adapter.notifyDataSetChanged()
        }
        snackbar.setActionTextColor(requireContext().getColor(R.color.brand_accent))
        snackbar.show()
    }

    private fun setupFiltersAndSearch() {
        // Kolom pencarian real-time
        binding.etSearchMenu.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setSearchQuery(s?.toString().orEmpty())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Filter chips kategori
        binding.chipGroupCategory.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isEmpty()) {
                viewModel.setCategoryFilter("Semua")
                return@setOnCheckedStateChangeListener
            }

            when (checkedIds.first()) {
                R.id.chipDimsum -> viewModel.setCategoryFilter("Dimsum")
                R.id.chipNasi -> viewModel.setCategoryFilter("Olahan Nasi")
                R.id.chipMinuman -> viewModel.setCategoryFilter("Minuman")
                R.id.chipDessert -> viewModel.setCategoryFilter("Dessert")
                else -> viewModel.setCategoryFilter("Semua")
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshMenu.setColorSchemeColors(
            requireContext().getColor(R.color.brand_primary)
        )
        binding.swipeRefreshMenu.setOnRefreshListener {
            viewModel.loadProducts()
        }
    }

    private fun observeData() {
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.swipeRefreshMenu.isRefreshing = loading
        }

        viewModel.filteredProducts.observe(viewLifecycleOwner) { products ->
            adapter.updateData(products)
            if (products.isEmpty()) {
                binding.tvEmptyMenu.show()
            } else {
                binding.tvEmptyMenu.gone()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
