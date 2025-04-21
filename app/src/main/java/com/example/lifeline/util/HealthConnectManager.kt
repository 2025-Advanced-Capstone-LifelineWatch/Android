package com.example.lifeline.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.BloodPressureRecord
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

    // HealthConnect이 사용 가능한지 확인
    suspend fun isAvailable(): Boolean {
        return HealthConnectClient.getSdkStatus(context, PROVIDER_PACKAGE_NAME) ==
                HealthConnectClient.SDK_AVAILABLE
    }

    // HealthConnectClient 객체 반환
    fun getClient(): HealthConnectClient =
        HealthConnectClient.getOrCreate(context)

    // 혈압 데이터를 읽어오는 함수
    suspend fun readBloodPressure(): List<BloodPressureRecord> {
        val client = getClient()  // HealthConnectClient 객체 생성

        // 시간 범위 설정: 어제부터 오늘까지
        val startTime = ZonedDateTime.now().minusDays(1).toInstant()
        val endTime = ZonedDateTime.now().toInstant()
        // 시간 범위 필터 설정
        val timeRangeFilter = TimeRangeFilter.between(startTime, endTime)

        // ReadRecordsRequest에 필터 적용
        val response = client.readRecords<BloodPressureRecord>(
            ReadRecordsRequest(
                recordType = BloodPressureRecord::class,  // 혈압 데이터 타입
                timeRangeFilter = timeRangeFilter,  // 시간 범위 필터
                ascendingOrder = false  // 내림차순으로 정렬
            )
        )

        return response.records  // BloodPressureRecord 목록 반환
    }

    // Health Connect 설치 페이지로 이동
    fun redirectToInstallPage() {
        val uri = Uri.parse("market://details?id=$PROVIDER_PACKAGE_NAME")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.android.vending")
            putExtra("overlay", true)
            putExtra("callerId", context.packageName)
        }
        context.startActivity(intent)
    }

    suspend fun getGrantedPermissions(client: HealthConnectClient): Set<String> {
        return client.permissionController.getGrantedPermissions()  // 권한 반환
    }
}
