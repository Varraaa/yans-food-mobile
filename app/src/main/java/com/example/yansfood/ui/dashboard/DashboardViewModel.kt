package com.example.yansfood.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yansfood.data.model.DashboardResponse
import com.example.yansfood.data.repository.DashboardRepository
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: DashboardRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _dashboardData = MutableLiveData<DashboardResponse>()
    val dashboardData: LiveData<DashboardResponse> = _dashboardData

    fun loadDashboardData() {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.getDashboardData()
            _isLoading.value = false
            result.onSuccess { data ->
                _dashboardData.value = data
            }
        }
    }
}
