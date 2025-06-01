package com.example.lifeline.ui.main

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.OxygenSaturationRecord
import androidx.health.connect.client.records.RespiratoryRateRecord
import androidx.health.connect.client.records.StepsRecord
import com.example.lifeline.LifelineApp
import com.example.lifeline.service.HealthDataService
import com.example.lifeline.R
import com.example.lifeline.data.repository.HealthRepository
import com.example.lifeline.ui.home.HomeActivity
import com.example.lifeline.ui.login.LoginActivity
import com.example.lifeline.ui.signup.SignupActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private lateinit var requestPermissions: ActivityResultLauncher<Set<String>>
    private lateinit var healthConnectClient: HealthConnectClient

    private val PERMISSIONS = setOf(
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getWritePermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getWritePermission(StepsRecord::class),
        HealthPermission.getReadPermission(BloodPressureRecord::class),
        HealthPermission.getWritePermission(BloodPressureRecord::class),
        HealthPermission.getReadPermission(BodyTemperatureRecord::class),
        HealthPermission.getWritePermission(BodyTemperatureRecord::class),
        HealthPermission.getReadPermission(RespiratoryRateRecord::class),
        HealthPermission.getWritePermission(RespiratoryRateRecord::class),
        HealthPermission.getReadPermission(OxygenSaturationRecord::class),
        HealthPermission.getWritePermission(OxygenSaturationRecord::class)

    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val token = prefs.getString("token", null)

        if (!token.isNullOrEmpty()) {
            // 토큰이 있다면 홈 화면으로
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish() // MainActivity 종료
        }
        // 권한 요청 런처 등록
        requestPermissions = registerForActivityResult(
            PermissionController.createRequestPermissionResultContract()
        ) { granted ->
            if (granted.containsAll(PERMISSIONS)) {
                showInstalledPopup()
            } else {
                showPermissionDeniedDialog()
            }
        }
        requestNotificationPermissionIfNeeded() // Health Connect 권한 외에 알림 권한도 요청

        // 헬스 커넥트 클라이언트 준비 및 권한 확인
        CoroutineScope(Dispatchers.Main).launch {
            val status = HealthConnectClient.getSdkStatus(this@MainActivity, "com.google.android.apps.healthdata")

            when (status) {
                HealthConnectClient.SDK_UNAVAILABLE -> {
                    Log.e("HealthConnect", "Health Connect is not available.")
                    return@launch
                }

                HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> {
                    val uriString = "market://details?id=com.google.android.apps.healthdata&url=healthconnect%3A%2F%2Fonboarding"
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setPackage("com.android.vending")
                        data = Uri.parse(uriString)
                        putExtra("overlay", true)
                        putExtra("callerId", packageName)
                    }
                    startActivity(intent)
                    return@launch
                }
            }

            healthConnectClient = HealthConnectClient.getOrCreate(this@MainActivity)

            val granted = healthConnectClient.permissionController.getGrantedPermissions()
            if (!granted.containsAll(PERMISSIONS)) {
                requestPermissions.launch(PERMISSIONS)
            } else {
        //        showInstalledPopup()
            }
        }

        // 로그인/회원가입 버튼
        findViewById<Button>(R.id.btn_login).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        findViewById<Button>(R.id.btn_signup).setOnClickListener {
            val token = LifelineApp.fcmToken
            if (token.isNullOrEmpty()) {
                Toast.makeText(this, "FCM 토큰이 아직 준비되지 않았습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Toast.makeText(this, token, Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, SignupActivity::class.java))
        }

        startHealthDataService()
    }

    private fun showInstalledPopup() {
        AlertDialog.Builder(this)
            .setTitle("헬스 커넥트")
            .setMessage("헬스 커넥트가 설치되어 있고, 권한이 부여되었습니다.")
            .setPositiveButton("확인") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("권한 부족")
            .setMessage("필수 권한이 거부되었습니다. Health Connect 설정에서 수동으로 권한을 허용해주세요.")
            .setPositiveButton("설정으로 이동") { _, _ ->
                val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:com.google.android.apps.healthdata")
                }
                startActivity(intent)
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun startHealthDataService() {
        val serviceIntent = Intent(this, HealthDataService::class.java)
        startForegroundService(serviceIntent)
    }

}
