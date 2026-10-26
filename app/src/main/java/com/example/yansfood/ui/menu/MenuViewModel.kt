package com.example.yansfood.ui.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yansfood.data.model.Product
import com.example.yansfood.data.repository.ProductRepository
import kotlinx.coroutines.launch

class MenuViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val allProducts = mutableListOf<Product>()

    private val _filteredProducts = MutableLiveData<List<Product>>()
    val filteredProducts: LiveData<List<Product>> = _filteredProducts

    private var currentCategory = "Semua"
    private var currentQuery = ""

    fun loadProducts() {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.getProducts()
            _isLoading.value = false
            result.onSuccess { list ->
                allProducts.clear()
                allProducts.addAll(list)
                applyFilters()
            }
        }
    }

    fun setCategoryFilter(category: String) {
        currentCategory = category
        applyFilters()
    }

    fun setSearchQuery(query: String) {
        currentQuery = query
        applyFilters()
    }

    private fun applyFilters() {
        val filtered = allProducts.filter { product ->
            val matchesCategory = if (currentCategory == "Semua") {
                true
            } else {
                product.category.equals(currentCategory, ignoreCase = true)
            }

            val matchesQuery = if (currentQuery.isBlank()) {
                true
            } else {
                product.name.contains(currentQuery, ignoreCase = true) ||
                product.category.contains(currentQuery, ignoreCase = true)
            }

            matchesCategory && matchesQuery
        }
        _filteredProducts.value = filtered
    }

    fun toggleProductStock(product: Product, isAvailable: Boolean) {
        viewModelScope.launch {
            repository.updateProductStock(product.id, isAvailable)
        }
    }
}
