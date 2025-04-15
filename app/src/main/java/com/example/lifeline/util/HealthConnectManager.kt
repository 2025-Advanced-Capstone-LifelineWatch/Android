package com.example.lifeline.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord

class HealthConnectManager(
    private val context: Context
) {
    companion object {
        const val PROVIDER_PACKAGE_NAME = "com.google.android.apps.healthdata"

        val REQUIRED_PERMISSIONS = setOf(
            HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getWritePermission(StepsRecord::class),
            HealthPermission.getReadPermission(HeartRateRecord::class),
            HealthPermission.getWritePermission(HeartRateRecord::class)
        )
    }

    suspend fun isAvailable(): Boolean {
        return HealthConnectClient.getSdkStatus(context, PROVIDER_PACKAGE_NAME) ==
                HealthConnectClient.SDK_AVAILABLE
    }

    fun getClient(): HealthConnectClient =
        HealthConnectClient.getOrCreate(context)

    suspend fun getGrantedPermissions(client: HealthConnectClient): Set<String> {
        return client.permissionController.getGrantedPermissions()
    }

    fun redirectToInstallPage() {
        val uri = Uri.parse("market://details?id=$PROVIDER_PACKAGE_NAME&url=healthconnect%3A%2F%2Fonboarding")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.android.vending")
            putExtra("overlay", true)
            putExtra("callerId", context.packageName)
        }
        context.startActivity(intent)
    }
}
