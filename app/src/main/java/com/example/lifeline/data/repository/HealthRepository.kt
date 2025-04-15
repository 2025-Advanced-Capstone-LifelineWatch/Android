package com.example.lifeline.data.repository

import com.example.lifeline.util.HealthConnectManager
import java.time.Instant
import java.time.ZoneOffset
import java.time.Duration
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.metadata.Metadata // ✅ 올바른 경로!

class HealthRepository(
    private val manager: HealthConnectManager
) {

    suspend fun isAvailable(): Boolean = manager.isAvailable()

    fun getClient(): HealthConnectClient = manager.getClient()

    suspend fun hasPermissions(client: HealthConnectClient): Boolean {
        val granted = manager.getGrantedPermissions(client)
        return granted.containsAll(HealthConnectManager.REQUIRED_PERMISSIONS)
    }
}