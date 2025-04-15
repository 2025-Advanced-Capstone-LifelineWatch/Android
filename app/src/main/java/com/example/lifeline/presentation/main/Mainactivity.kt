package com.example.lifeline.presentation.main

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.health.connect.client.PermissionController
import androidx.lifecycle.lifecycleScope
import com.example.lifeline.util.HealthConnectManager
import com.example.lifeline.data.repository.HealthRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    private lateinit var viewModel: HealthConnectViewModel
    private val manager by lazy { HealthConnectManager(this) }
    private val repository by lazy { HealthRepository(manager) }

    private val permissionLauncher = registerForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        if (granted.containsAll(HealthConnectManager.REQUIRED_PERMISSIONS)) {
            Toast.makeText(this, "✅ 권한 허용됨", Toast.LENGTH_SHORT).show()
            // viewModel.insertTestSteps() // ← 필요 없다면 주석처리
        } else {
            Toast.makeText(this, "❌ 권한 거부됨", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val textView = TextView(this).apply {
            text = "헬스 커넥트 초기화 중..."
            textSize = 18f
            setPadding(50, 100, 50, 100)
        }
        setContentView(textView)

        viewModel = HealthConnectViewModel(repository)
        viewModel.init()

        lifecycleScope.launch {
            viewModel.isAvailable.collectLatest { available ->
                if (!available) {
                    manager.redirectToInstallPage()
                    textView.text = "헬스 커넥트가 설치되어 있지 않음"
                }
            }
        }
    }
}
