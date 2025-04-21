package com.example.lifeline.presentation.main

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.lifeline.util.HealthConnectManager
import com.example.lifeline.data.repository.HealthRepository
import com.example.lifeline.R
import com.example.lifeline.presentation.login.LoginActivity
import com.example.lifeline.ui.signup.SignupActivity
import com.example.lifeline.ui.main.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MainViewModel
    private val manager by lazy { HealthConnectManager(this) }
    private val repository by lazy { HealthRepository(manager) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ViewModel 초기화
        viewModel = MainViewModel(repository)
        viewModel.init()

        // 버튼 클릭 리스너 설정
        val loginButton: Button = findViewById(R.id.btn_login)
        val signupButton: Button = findViewById(R.id.btn_signup)

        loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        signupButton.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        // 헬스 커넥트 상태 확인 (TextView 없이 팝업만 사용)
        lifecycleScope.launch {
            viewModel.isAvailable.collectLatest { available ->
                if (!available) {
                    manager.redirectToInstallPage()
                } else {
                    showInstalledPopup()
                }
            }
        }
    }

    private fun showInstalledPopup() {
        AlertDialog.Builder(this)
            .setTitle("헬스 커넥트")
            .setMessage("헬스 커넥트가 설치되어 있습니다.")
            .setPositiveButton("확인") { dialog, _ -> dialog.dismiss() }
            .show()
    }

}
