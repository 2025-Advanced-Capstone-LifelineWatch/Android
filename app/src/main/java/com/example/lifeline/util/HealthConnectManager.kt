//package com.example.lifeline.util
//
//import android.content.Context
//import android.content.Intent
//import android.net.Uri
//import androidx.health.connect.client.HealthConnectClient
//import androidx.health.connect.client.permission.HealthPermission
//import androidx.health.connect.client.records.BloodPressureRecord
//import androidx.health.connect.client.records.BodyTemperatureRecord
//import androidx.health.connect.client.records.HeartRateRecord
//import androidx.health.connect.client.records.OxygenSaturationRecord
//import androidx.health.connect.client.records.RespiratoryRateRecord
//import androidx.health.connect.client.records.StepsRecord
//import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
//import androidx.health.connect.client.request.ReadRecordsRequest
//import androidx.health.connect.client.time.TimeRangeFilter
//import java.time.Instant
//import java.time.ZonedDateTime
//
//class HealthConnectManager(private val context: Context) {
//
//    companion object {
//        const val PROVIDER_PACKAGE_NAME = "com.google.android.apps.healthdata"
//
//        val REQUIRED_PERMISSIONS = setOf(
//            HealthPermission.getReadPermission(StepsRecord::class),
//            HealthPermission.getWritePermission(StepsRecord::class),
//            HealthPermission.getReadPermission(HeartRateRecord::class),
//            HealthPermission.getWritePermission(HeartRateRecord::class),
//            HealthPermission.getReadPermission(BloodPressureRecord::class),
//            HealthPermission.getWritePermission(BloodPressureRecord::class),
//            HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
//            HealthPermission.getWritePermission(TotalCaloriesBurnedRecord::class),
//            HealthPermission.getReadPermission(RespiratoryRateRecord::class),
//            HealthPermission.getWritePermission(RespiratoryRateRecord::class),
//            HealthPermission.getReadPermission(OxygenSaturationRecord::class),
//            HealthPermission.getWritePermission(OxygenSaturationRecord::class)
//        )
//    }
//
//    suspend fun isAvailable(): Boolean {
//        return HealthConnectClient.getSdkStatus(context, PROVIDER_PACKAGE_NAME) ==
//                HealthConnectClient.SDK_AVAILABLE
//    }
//
//    fun getClient(): HealthConnectClient = HealthConnectClient.getOrCreate(context)
//
//    fun redirectToInstallPage() {
//        val uri = Uri.parse("market://details?id=$PROVIDER_PACKAGE_NAME")
//        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
//            setPackage("com.android.vending")
//            putExtra("overlay", true)
//            putExtra("callerId", context.packageName)
//        }
//        context.startActivity(intent)
//    }
//
//    suspend fun readBloodPressure(): List<BloodPressureRecord> {
//        val client = getClient()
//        val startTime = ZonedDateTime.now().minusDays(1).toInstant()
//        val endTime = ZonedDateTime.now().toInstant()
//        val timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
//
//        val response = client.readRecords(
//            ReadRecordsRequest(
//                recordType = BloodPressureRecord::class,
//                timeRangeFilter = timeRangeFilter,
//                ascendingOrder = false
//            )
//        )
//        return response.records
//    }
//
//    suspend fun readTemperature(): List<BodyTemperatureRecord> {
//        val client = getClient()
//        val startTime = ZonedDateTime.now().minusDays(1).toInstant()
//        val endTime = ZonedDateTime.now().toInstant()
//        val timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
//
//        val response = client.readRecords(
//            ReadRecordsRequest(
//                recordType = BodyTemperatureRecord::class,
//                timeRangeFilter = timeRangeFilter,
//                ascendingOrder = false
//            )
//        )
//        return response.records
//    }
//
//    suspend fun readCalories(dailyStart: Instant, endTime: Instant): List<TotalCaloriesBurnedRecord> {
//        val client = getClient()
//        val startTime = ZonedDateTime.now().minusDays(1).toInstant()
//        val endTime = ZonedDateTime.now().toInstant()
//        val timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
//
//        val response = client.readRecords(
//            ReadRecordsRequest(
//                recordType = TotalCaloriesBurnedRecord::class,
//                timeRangeFilter = timeRangeFilter,
//                ascendingOrder = false
//            )
//        )
//        return response.records
//    }
//
//    suspend fun readRespiratoryRate(startTime: Instant, endTime: Instant): List<RespiratoryRateRecord> {
//        val client = getClient()
//        val startTime = ZonedDateTime.now().minusDays(1).toInstant()
//        val endTime = ZonedDateTime.now().toInstant()
//        val timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
//
//        val response = client.readRecords(
//            ReadRecordsRequest(
//                recordType = RespiratoryRateRecord::class,
//                timeRangeFilter = timeRangeFilter,
//                ascendingOrder = false
//            )
//        )
//        return response.records
//    }
//
//    suspend fun readOxygenSaturation(startTime: Instant, endTime: Instant): List<OxygenSaturationRecord> {
//        val client = getClient()
//        val startTime = ZonedDateTime.now().minusDays(1).toInstant()
//        val endTime = ZonedDateTime.now().toInstant()
//        val timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
//
//        val response = client.readRecords(
//            ReadRecordsRequest(
//                recordType = OxygenSaturationRecord::class,
//                timeRangeFilter = timeRangeFilter,
//                ascendingOrder = false
//            )
//        )
//        return response.records
//    }
//
//    suspend fun readSteps(dailyStart: Instant, endTime: Instant): List<StepsRecord> {
//        val client = getClient()
//        val startTime = ZonedDateTime.now().minusDays(1).toInstant()
//        val endTime = ZonedDateTime.now().toInstant()
//        val timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
//
//        val response = client.readRecords(
//            ReadRecordsRequest(
//                recordType = StepsRecord::class,
//                timeRangeFilter = timeRangeFilter,
//                ascendingOrder = false
//            )
//        )
//        return response.records
//    }
//
//    suspend fun readHeartRate(startTime: Instant, endTime: Instant): List<HeartRateRecord> {
//        val client = getClient()
//        val startTime = ZonedDateTime.now().minusDays(1).toInstant()
//        val endTime = ZonedDateTime.now().toInstant()
//        val timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
//
//        val response = client.readRecords(
//            ReadRecordsRequest(
//                recordType = HeartRateRecord::class,
//                timeRangeFilter = timeRangeFilter,
//                ascendingOrder = false
//            )
//        )
//        return response.records
//    }
//
//    suspend fun getGrantedPermissions(): Set<String> {
//        return getClient().permissionController.getGrantedPermissions()
//    }
//
//
//}

package com.example.lifeline.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.OxygenSaturationRecord
import androidx.health.connect.client.records.RespiratoryRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant
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
            HealthPermission.getWritePermission(BloodPressureRecord::class),
            HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
            HealthPermission.getWritePermission(TotalCaloriesBurnedRecord::class),
            HealthPermission.getReadPermission(RespiratoryRateRecord::class),
            HealthPermission.getWritePermission(RespiratoryRateRecord::class),
            HealthPermission.getReadPermission(OxygenSaturationRecord::class),
            HealthPermission.getWritePermission(OxygenSaturationRecord::class)
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

    suspend fun readBloodPressure(startTime: Instant? = null, endTime: Instant? = null): List<BloodPressureRecord> {
        val client = getClient()
        val actualStartTime = startTime ?: ZonedDateTime.now().minusDays(1).toInstant()
        val actualEndTime = endTime ?: ZonedDateTime.now().toInstant()
        val timeRangeFilter = TimeRangeFilter.between(actualStartTime, actualEndTime)

        val response = client.readRecords(
            ReadRecordsRequest(
                recordType = BloodPressureRecord::class,
                timeRangeFilter = timeRangeFilter,
                ascendingOrder = false
            )
        )
        return response.records
    }

    suspend fun readTemperature(startTime: Instant? = null, endTime: Instant? = null): List<BodyTemperatureRecord> {
        val client = getClient()
        val actualStartTime = startTime ?: ZonedDateTime.now().minusDays(1).toInstant()
        val actualEndTime = endTime ?: ZonedDateTime.now().toInstant()
        val timeRangeFilter = TimeRangeFilter.between(actualStartTime, actualEndTime)

        val response = client.readRecords(
            ReadRecordsRequest(
                recordType = BodyTemperatureRecord::class,
                timeRangeFilter = timeRangeFilter,
                ascendingOrder = false
            )
        )
        return response.records
    }

    suspend fun readCalories(startTime: Instant, endTime: Instant): List<TotalCaloriesBurnedRecord> {
        val client = getClient()
        val timeRangeFilter = TimeRangeFilter.between(startTime, endTime)

        val response = client.readRecords(
            ReadRecordsRequest(
                recordType = TotalCaloriesBurnedRecord::class,
                timeRangeFilter = timeRangeFilter,
                ascendingOrder = false
            )
        )
        return response.records
    }

    suspend fun readRespiratoryRate(startTime: Instant, endTime: Instant): List<RespiratoryRateRecord> {
        val client = getClient()
        val timeRangeFilter = TimeRangeFilter.between(startTime, endTime)

        val response = client.readRecords(
            ReadRecordsRequest(
                recordType = RespiratoryRateRecord::class,
                timeRangeFilter = timeRangeFilter,
                ascendingOrder = false
            )
        )
        return response.records
    }

    suspend fun readOxygenSaturation(startTime: Instant, endTime: Instant): List<OxygenSaturationRecord> {
        val client = getClient()
        val timeRangeFilter = TimeRangeFilter.between(startTime, endTime)

        val response = client.readRecords(
            ReadRecordsRequest(
                recordType = OxygenSaturationRecord::class,
                timeRangeFilter = timeRangeFilter,
                ascendingOrder = false
            )
        )
        return response.records
    }

    suspend fun readSteps(startTime: Instant, endTime: Instant): List<StepsRecord> {
        val client = getClient()
        val timeRangeFilter = TimeRangeFilter.between(startTime, endTime)

        val response = client.readRecords(
            ReadRecordsRequest(
                recordType = StepsRecord::class,
                timeRangeFilter = timeRangeFilter,
                ascendingOrder = false
            )
        )
        return response.records
    }

    suspend fun readHeartRate(startTime: Instant, endTime: Instant): List<HeartRateRecord> {
        val client = getClient()
        val timeRangeFilter = TimeRangeFilter.between(startTime, endTime)

        val response = client.readRecords(
            ReadRecordsRequest(
                recordType = HeartRateRecord::class,
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
