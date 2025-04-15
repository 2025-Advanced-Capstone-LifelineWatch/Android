package com.example.lifeline.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeline.data.repository.HealthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.health.connect.client.HealthConnectClient

class HealthConnectViewModel(
    private val repository: HealthRepository
) : ViewModel() {

    private val _client = MutableStateFlow<HealthConnectClient?>(null)
    val client: StateFlow<HealthConnectClient?> = _client

    private val _hasPermission = MutableStateFlow(false)
    val hasPermission: StateFlow<Boolean> = _hasPermission

    private val _isAvailable = MutableStateFlow(false)
    val isAvailable: StateFlow<Boolean> = _isAvailable

    fun init() {
        viewModelScope.launch {
            val available = repository.isAvailable()
            _isAvailable.value = available
            if (!available) return@launch

            val client = repository.getClient()
            _client.value = client

            _hasPermission.value = repository.hasPermissions(client)
        }
    }

}
