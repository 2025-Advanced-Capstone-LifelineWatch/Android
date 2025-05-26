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
import com.example.lifeline.network.dto.VerifyRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class VerificationActivity : AppCompatActivity() {

    private lateinit var editId: EditText
    private lateinit var editPhone: EditText
    private lateinit var btnVerify: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_edit_info)

        editPhone = findViewById(R.id.edit_phone)
        editId = findViewById(R.id.edit_id)
        btnVerify = findViewById(R.id.btn_verify)

        // 초기 버튼 비활성화
        btnVerify.isEnabled = false
        btnVerify.setTextColor(Color.GRAY)

        // 텍스트 변경 감지 리스너 등록
        editId.addTextChangedListener(textWatcher)
        editPhone.addTextChangedListener(textWatcher)

        val destination = intent.getStringExtra("destination")

        btnVerify.setOnClickListener {
            val inputId = editId.text.toString().trim()
            val inputPhone = editPhone.text.toString().trim()

            if (inputId.isBlank() || inputPhone.isBlank()) {
                Toast.makeText(this, "아이디와 휴대폰 번호를 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val request = VerifyRequest(
                        loginId = inputId,
                        phoneNumber = inputPhone
                    )
                    val response = RetrofitClient.apiService.verifyIdentify(request)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            when (destination) {
                                "edit_profile" -> {
                                    startActivity(Intent(this@VerificationActivity, EditProfileActivity::class.java))
                                }
                                "change_password" -> {
                                    startActivity(Intent(this@VerificationActivity, PasswordChangeActivity::class.java))
                                }
                                else -> {
                                    Toast.makeText(this@VerificationActivity, "이동할 화면 정보가 없습니다: $destination", Toast.LENGTH_SHORT).show()
                                }
                            }
                            finish()
                        } else {
                            Toast.makeText(this@VerificationActivity, "입력하신 정보가 올바르지 않습니다", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: HttpException) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@VerificationActivity, "서버 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        e.printStackTrace()
                        Toast.makeText(this@VerificationActivity, "오류 발생: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            updateButtonState()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private fun updateButtonState() {
        val idFilled = editId.text.toString().trim().isNotEmpty()
        val phoneFilled = editPhone.text.toString().trim().isNotEmpty()

        if (idFilled && phoneFilled) {
            btnVerify.isEnabled = true
            btnVerify.setTextColor(Color.WHITE)
        } else {
            btnVerify.isEnabled = false
            btnVerify.setTextColor(Color.GRAY)
        }
    }
}
