package com.example.lifeline.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.ZonedDateTime

class HealthConnectManager(private val context: Context) {

    companion object {
        const val PROVIDER_PACKAGE_NAME = "com.google.android.apps.healthdata"

        val REQUIRED_PERMISSIONS = setOf(
            HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getWritePermission(StepsRecord::class),
            HealthPermission.getReadPermission(HeartRateRecord::class),
            HealthPermission.getWritePermission(HeartRateRecord::class),
            HealthPermission.getReadPermission(BloodPressureRecord::class),
            HealthPermission.getWritePermission(BloodPressureRecord::class)
        )
    }

    suspend fun isAvailable(): Boolean {
        return HealthConnectClient.getSdkStatus(context, PROVIDER_PACKAGE_NAME) ==
                HealthConnectClient.SDK_AVAILABLE
    }

    fun getClient(): HealthConnectClient = HealthConnectClient.getOrCreate(context)

    fun redirectToInstallPage() {
        val uri = Uri.parse("market://details?id=$PROVIDER_PACKAGE_NAME")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.android.vending")
            putExtra("overlay", true)
            putExtra("callerId", context.packageName)
        }
        context.startActivity(intent)
    }

    suspend fun readBloodPressure(): List<BloodPressureRecord> {
        val client = getClient()
        val startTime = ZonedDateTime.now().minusDays(1).toInstant()
        val endTime = ZonedDateTime.now().toInstant()
        val timeRangeFilter = TimeRangeFilter.between(startTime, endTime)

        val response = client.readRecords(
            ReadRecordsRequest(
                recordType = BloodPressureRecord::class,
                timeRangeFilter = timeRangeFilter,
                ascendingOrder = false
            )
        )
        return response.records
    }

    suspend fun getGrantedPermissions(): Set<String> {
        return getClient().permissionController.getGrantedPermissions()
    }
}
