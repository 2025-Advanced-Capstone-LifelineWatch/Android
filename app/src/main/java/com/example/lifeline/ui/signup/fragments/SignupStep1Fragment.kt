package com.example.lifeline.ui.signup.fragments

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.lifeline.R
import com.example.lifeline.network.RetrofitClient
import com.example.lifeline.network.dto.VerifyCodeRequest
import com.example.lifeline.ui.signup.SignupActivity
import com.example.lifeline.ui.signup.userSignupData
import kotlinx.coroutines.launch

class SignupStep1Fragment : Fragment() {

    private lateinit var inputName: EditText
    private lateinit var inputPhone: EditText
    private lateinit var inputCode: EditText
    private lateinit var authButton: Button
    private lateinit var nextButton: Button

    private var isAuthRequested = false
    private var isAuthVerified = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_signup_step1, container, false)

        inputName = view.findViewById(R.id.input_name)
        inputPhone = view.findViewById(R.id.input_PH)
        inputCode = view.findViewById(R.id.auth_code)
        authButton = view.findViewById(R.id.auth_button)
        nextButton = view.findViewById(R.id.btn_register_1)

        nextButton.isEnabled = false
        updateNextButtonState()

        inputName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateNextButtonState()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        authButton.setOnClickListener {
            val phone = inputPhone.text.toString().trim()

            if (phone.isEmpty()) {
                Toast.makeText(requireContext(), "휴대폰 번호를 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isAuthRequested) {
                lifecycleScope.launch {
                    try {
                        val response = RetrofitClient.authService.requestSmsCode(phone)
                        if (response.isSuccessful) {
                            Toast.makeText(requireContext(), "인증번호가 전송되었습니다", Toast.LENGTH_SHORT).show()
                            authButton.text = "인증번호 확인"
                            isAuthRequested = true
                        } else {
                            Toast.makeText(requireContext(), "전송 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "네트워크 오류: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                val code = inputCode.text.toString().trim()
                if (code.isEmpty()) {
                    Toast.makeText(requireContext(), "인증번호를 입력해주세요", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                lifecycleScope.launch {
                    try {
                        val response = RetrofitClient.authService.verifySmsCode(
                            VerifyCodeRequest(phone, code)
                        )
                        if (response.isSuccessful) {
                            Toast.makeText(requireContext(), "인증 성공", Toast.LENGTH_SHORT).show()
                            authButton.isEnabled = false
                            authButton.text = "인증 완료"
                            isAuthVerified = true
                            updateNextButtonState()
                        } else {
                            Toast.makeText(requireContext(), "인증 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "네트워크 오류: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        nextButton.setOnClickListener {
            val name = inputName.text.toString().trim()
            val phone = inputPhone.text.toString().trim()
            val verificationCode = inputCode.text.toString().trim()

            if (name.isEmpty() || phone.isEmpty() || verificationCode.isEmpty()) {
                Toast.makeText(requireContext(), "모든 정보를 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isAuthVerified || name.isEmpty()) {
                Toast.makeText(requireContext(), "이름과 휴대폰 인증을 먼저 해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            (activity as? SignupActivity)?.apply {
                userSignupData.name = name
                userSignupData.phoneNumber = phone
                userSignupData.verificationCode = verificationCode
                goToStep(2)
            }
        }

        return view
    }

    private fun updateNextButtonState() {
        val nameFilled = inputName.text.toString().trim().isNotEmpty()

        if (nameFilled && isAuthVerified) {
            nextButton.isEnabled = true
            nextButton.setTextColor(Color.WHITE)
        } else {
            nextButton.isEnabled = false
            nextButton.setTextColor(Color.BLACK)
        }
    }
}
