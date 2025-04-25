package com.example.lifeline.data.repository

import com.example.lifeline.util.HealthConnectManager
import androidx.health.connect.client.HealthConnectClient

class HealthRepository(
    private val manager: HealthConnectManager
) {

    suspend fun isAvailable(): Boolean = manager.isAvailable()

    fun getClient(): HealthConnectClient = manager.getClient()

    suspend fun hasPermissions(client: HealthConnectClient): Boolean {
        val granted = manager.getGrantedPermissions()
        return granted.containsAll(HealthConnectManager.REQUIRED_PERMISSIONS)
    }
}