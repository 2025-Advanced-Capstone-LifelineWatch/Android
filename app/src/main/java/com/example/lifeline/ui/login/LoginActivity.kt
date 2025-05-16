package com.example.lifeline.ui.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.lifeline.LifelineApp
import com.example.lifeline.R
import com.example.lifeline.network.RetrofitClient
import com.example.lifeline.network.dto.FcmTokenUpdate
import com.example.lifeline.network.dto.LoginRequest
import com.example.lifeline.ui.home.HomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {

    private lateinit var idInput: EditText
    private lateinit var pwInput: EditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        idInput = findViewById(R.id.input_id)
        pwInput = findViewById(R.id.input_pw)
        loginButton = findViewById(R.id.btn_login)

        loginButton.isEnabled = false // 초기 비활성화

        idInput.addTextChangedListener(textWatcher)
        pwInput.addTextChangedListener(textWatcher)

        loginButton.setOnClickListener {
            val id = idInput.text.toString()
            val pw = pwInput.text.toString()

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    Log.d("LoginDebug", "로그인 요청 시작")
                    val response = RetrofitClient.authService.login(LoginRequest(id, pw))
                    Log.d("LoginDebug", "로그인 응답 수신")

                    if (response.isSuccessful) {
                        Log.d("LoginDebug", "응답 성공: ${response.code()}")
                        val user = response.body()?.results?.firstOrNull()
                        Log.d("LoginDebug", "user 객체: $user")

                        if (user != null) {
                            val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
                            prefs.edit().apply {
                                putString("token", user.token)
                                putString("name", user.name)
                                putString("birthDate", user.birthDate)
                                putString("protectorName", user.protectorName)
                                putString("protectorContact", user.protectorContact)
                                putString("socialWorkerName", user.socialWorkerName)
                                putString("socialWorkerPhone", user.socialWorkerPhone)
                                putInt("userId", user.userId)
                                putBoolean("isSocialWorker", user.isSocialWorker)
                                apply()
                            }

                            Log.d("LoginDebug", "SharedPreferences 저장 완료")

                            launch(Dispatchers.IO) {
                                try {
                                    val fcmToken = LifelineApp.fcmToken ?: return@launch
                                    Log.d("LoginDebug", "FCM 토큰 PATCH 요청: $fcmToken")
                                    RetrofitClient.authService.updateFcmToken(FcmTokenUpdate(fcmToken))
                                } catch (e: Exception) {
                                    Log.e("LoginDebug", "FCM 토큰 업데이트 실패", e)
                                }
                            }

                            Toast.makeText(this@LoginActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)

                        } else {
                            Log.e("LoginDebug", "user 객체가 null입니다")
                            Toast.makeText(this@LoginActivity, "사용자 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.e("LoginDebug", "응답 실패: ${response.code()}, body: ${response.errorBody()?.string()}")
                        Toast.makeText(this@LoginActivity, "로그인 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }

                } catch (e: Exception) {
                    Log.e("LoginDebug", "예외 발생", e)
                    Toast.makeText(this@LoginActivity, "네트워크 오류 발생", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            updateLoginButtonState()
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private fun updateLoginButtonState() {
        val idFilled = idInput.text.toString().trim().isNotEmpty()
        val pwFilled = pwInput.text.toString().trim().isNotEmpty()
        loginButton.isEnabled = idFilled && pwFilled

        if (idFilled && pwFilled) {
            loginButton.setTextColor(Color.WHITE)
        } else
            loginButton.setTextColor(Color.BLACK)
    }
}
