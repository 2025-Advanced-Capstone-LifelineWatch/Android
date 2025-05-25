package com.example.lifeline.ui.signup.fragments

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.lifeline.LifelineApp
import com.example.lifeline.R
import com.example.lifeline.network.RetrofitClient
import com.example.lifeline.network.dto.SignupRequest
import com.example.lifeline.ui.signup.userSignupData
import com.google.gson.Gson
import kotlinx.coroutines.launch


class SignupStep3Fragment : Fragment() {

    private lateinit var inputAddress: EditText
    private lateinit var inputSocialId: EditText
    private lateinit var inputDrn: EditText
    private lateinit var inputProtectorName: EditText
    private lateinit var inputProtectorContact: EditText
    private lateinit var submitButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_signup_step3, container, false)

        inputAddress = view.findViewById(R.id.input_address)
        inputSocialId = view.findViewById(R.id.input_social)
        inputDrn = view.findViewById(R.id.input_dpn)
        inputProtectorName = view.findViewById(R.id.input_protect_name)
        inputProtectorContact = view.findViewById(R.id.input_protect_PH)
        submitButton = view.findViewById(R.id.btn_register_3)

        submitButton.isEnabled = false
        updateSubmitButtonState()

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateSubmitButtonState()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        inputAddress.addTextChangedListener(watcher)
        inputSocialId.addTextChangedListener(watcher)
        inputDrn.addTextChangedListener(watcher)
        inputProtectorName.addTextChangedListener(watcher)
        inputProtectorContact.addTextChangedListener(watcher)

        submitButton.setOnClickListener {
            val address = inputAddress.text.toString().trim()
            val socialWorkerId = inputSocialId.text.toString().trim().toLongOrNull()
            val drn = inputDrn.text.toString().trim()
            val protectorName = inputProtectorName.text.toString().trim()
            val protectorContact = inputProtectorContact.text.toString().trim()

            if (address.isEmpty() || socialWorkerId == null) {
                Toast.makeText(requireContext(), "주소와 사회복지사 ID는 필수입니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val birthDate = extractBirthDateFromRRN(userSignupData.rrn)
            userSignupData.birthDate = birthDate

            val request = SignupRequest(
                name = userSignupData.name,
                loginId = userSignupData.loginId,
                email = userSignupData.email,
                password = userSignupData.password,
                phoneNumber = userSignupData.phoneNumber,
                address = address,
                rrn = userSignupData.rrn,
                drn = drn,
                socialWorkerId = socialWorkerId,
                birthDate = birthDate,
                gender = userSignupData.gender,
                protectorContact = protectorContact,
                protectorName = protectorName,
                verificationCode = userSignupData.verificationCode,
                fcmToken = LifelineApp.fcmToken ?: ""
            )

            val json = Gson().toJson(request)
            Log.d("SignupRequest JSON", json)

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.authService.signup(request)
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "회원가입 완료", Toast.LENGTH_SHORT).show()
                        activity?.finish()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("Signup", "회원가입 실패: $errorBody")
                        Toast.makeText(requireContext(), "회원가입 실패: $errorBody", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "오류 발생: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }

    private fun extractBirthDateFromRRN(rrn: String): String {
        return if (rrn.length >= 7) {
            val yearPrefix = when (rrn[6]) {
                '1', '2' -> "19"
                '3', '4' -> "20"
                else -> ""
            }
            val year = yearPrefix + rrn.substring(0, 2)
            val month = rrn.substring(2, 4)
            val day = rrn.substring(4, 6)
            "$year/$month/$day"  // yyyy/MM/dd 포맷으로 변경
        } else {
            ""
        }
    }

    private fun updateSubmitButtonState() {
        val addressFilled = inputAddress.text.toString().trim().isNotEmpty()
        val socialIdValid = inputSocialId.text.toString().trim().toLongOrNull() != null

        if (addressFilled && socialIdValid) {
            submitButton.isEnabled = true
            submitButton.setBackgroundColor(Color.BLACK)
            submitButton.setTextColor(Color.WHITE)
        } else {
            submitButton.isEnabled = false
            submitButton.setBackgroundColor(Color.WHITE)
            submitButton.setTextColor(Color.BLACK)
        }
    }
}
