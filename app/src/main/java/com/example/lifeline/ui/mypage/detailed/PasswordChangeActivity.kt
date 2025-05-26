package com.example.lifeline.ui.mypage.detailed

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lifeline.R
import com.example.lifeline.network.RetrofitClient
import com.example.lifeline.network.dto.PasswordUpdate
import com.example.lifeline.ui.login.LoginActivity
import com.example.lifeline.ui.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class PasswordChangeActivity : AppCompatActivity() {

    private lateinit var input_pw: EditText
    private lateinit var input_pw_re: EditText
    private lateinit var btnUpdate: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_edit_password)

        input_pw = findViewById(R.id.input_pw)
        input_pw_re = findViewById(R.id.input_pw_re)
        btnUpdate = findViewById(R.id.btn_register_2)

        // 초기 버튼 상태
        btnUpdate.isEnabled = false
        btnUpdate.setTextColor(Color.GRAY)

        // 텍스트 변경 리스너 등록
        input_pw.addTextChangedListener(textWatcher)
        input_pw_re.addTextChangedListener(textWatcher)

        btnUpdate.setOnClickListener {
            val inputPassword = input_pw.text.toString()
            val reInputPassword = input_pw_re.text.toString()

            if (inputPassword == reInputPassword) {
                Toast.makeText(this, "현재 비밀번호와 새 비밀번호가 같습니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = PasswordUpdate(
                currentPassword = inputPassword,
                newPassword = reInputPassword
            )

            btnUpdate.isEnabled = false
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitClient.apiService.updatePassword(request)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@PasswordChangeActivity, "비밀번호가 성공적으로 변경되었습니다", Toast.LENGTH_SHORT).show()

                            val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
                            prefs.edit().clear().apply()

                            val intent = Intent(this@PasswordChangeActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK  // 스택 제거
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@PasswordChangeActivity, "비밀번호 변경 실패", Toast.LENGTH_SHORT).show()
                        }
                        btnUpdate.isEnabled = true
                    }
                } catch (e: HttpException) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@PasswordChangeActivity, "서버 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                        btnUpdate.isEnabled = true
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@PasswordChangeActivity, "알 수 없는 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                        btnUpdate.isEnabled = true
                    }
                }
            }
        }
    }

    // 텍스트 변경 감지 리스너
    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            updateButtonState()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    // 버튼 활성화 상태 갱신
    private fun updateButtonState() {
        val pwFilled = input_pw.text.toString().trim().isNotEmpty()
        val rePwFilled = input_pw_re.text.toString().trim().isNotEmpty()

        if (pwFilled && rePwFilled) {
            btnUpdate.isEnabled = true
            btnUpdate.setTextColor(Color.WHITE)
        } else {
            btnUpdate.isEnabled = false
            btnUpdate.setTextColor(Color.GRAY)
        }
    }
}
