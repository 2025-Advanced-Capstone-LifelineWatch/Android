package com.example.lifeline.ui.main

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.PermissionController
import com.example.lifeline.R
import com.example.lifeline.data.repository.HealthRepository
import com.example.lifeline.ui.login.LoginActivity
import com.example.lifeline.ui.signup.SignupActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var requestPermissions: ActivityResultLauncher<Set<String>>
    private lateinit var healthConnectClient: HealthConnectClient

    private val PERMISSIONS = setOf(
        HealthPermission.getReadPermission(androidx.health.connect.client.records.HeartRateRecord::class),
        HealthPermission.getWritePermission(androidx.health.connect.client.records.HeartRateRecord::class),
        HealthPermission.getReadPermission(androidx.health.connect.client.records.StepsRecord::class),
        HealthPermission.getWritePermission(androidx.health.connect.client.records.StepsRecord::class),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                showInstalledPopup()
            }
        }

        // 로그인/회원가입 버튼
        findViewById<Button>(R.id.btn_login).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        findViewById<Button>(R.id.btn_signup).setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun showInstalledPopup() {
        AlertDialog.Builder(this)
            .setTitle("헬스 커넥트")
            .setMessage("헬스 커넥트가 설치되어 있고, 권한이 부여되었습니다.")
            .setPositiveButton("확인") { dialog, _ -> dialog.dismiss() }
            .show()
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
}
