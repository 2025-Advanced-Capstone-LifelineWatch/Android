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
import com.example.lifeline.R
import com.example.lifeline.ui.signup.SignupActivity
import com.example.lifeline.ui.signup.userSignupData

class SignupStep2Fragment : Fragment() {

    private lateinit var inputId: EditText
    private lateinit var inputPw: EditText
    private lateinit var inputPwRe: EditText
    private lateinit var inputRrn: EditText
    private lateinit var maleButton: Button
    private lateinit var femaleButton: Button
    private lateinit var nextButton: Button

    private var selectedGender: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_signup_step2, container, false)
        val passwordPattern = Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{10,}$")

        inputId = view.findViewById(R.id.input_id)
        inputPw = view.findViewById(R.id.input_pw)
        inputPwRe = view.findViewById(R.id.input_pw_re)
        inputRrn = view.findViewById(R.id.input_rrn)
        maleButton = view.findViewById(R.id.btn_male)
        femaleButton = view.findViewById(R.id.btn_female)
        nextButton = view.findViewById(R.id.btn_register_2)

        // 초기화 시 버튼 스타일도 기본 설정
        applyGenderButtonStyle(null)

        setupInputWatchers()
        updateNextButtonState()

        maleButton.setOnClickListener {
            selectGender("남")
        }

        femaleButton.setOnClickListener {
            selectGender("여")
        }

        nextButton.setOnClickListener {
            val id = inputId.text.toString().trim()
            val pw = inputPw.text.toString().trim()
            val pwRe = inputPwRe.text.toString().trim()
            val rrn = inputRrn.text.toString().trim()

            when {
                id.isEmpty() -> showToast("아이디를 입력해주세요.")
                pw.isEmpty() -> showToast("비밀번호를 입력해주세요.")
                !pw.matches(passwordPattern) -> showToast("비밀번호는 영문과 숫자를 조합하여 10자 이상 입력해주세요.")

                pwRe.isEmpty() -> showToast("비밀번호 확인을 입력해주세요.")
                pw != pwRe -> {
                    showToast("비밀번호가 일치하지 않습니다.")
                    return@setOnClickListener
                }
                rrn.isEmpty() -> showToast("주민번호를 입력해주세요.")
                rrn.length != 13 -> showToast("정확한 주민번호를 입력하세요.")
                rrn[6] !in listOf('1', '2', '3', '4') -> showToast("정확한 주민번호를 입력하세요.")
                selectedGender == null -> showToast("성별을 선택해주세요.")
                else -> {
                    userSignupData.loginId = id
                    userSignupData.password = pw
                    userSignupData.rrn = rrn
                    userSignupData.gender = selectedGender!!
                    (activity as? SignupActivity)?.goToStep(3)
                }
            }
        }

        return view
    }

    private fun setupInputWatchers() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateNextButtonState()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        inputId.addTextChangedListener(watcher)
        inputPw.addTextChangedListener(watcher)
        inputPwRe.addTextChangedListener(watcher)
        inputRrn.addTextChangedListener(watcher)
    }

    private fun updateNextButtonState() {
        val idFilled = inputId.text.toString().trim().isNotEmpty()
        val pwFilled = inputPw.text.toString().trim().isNotEmpty()
        val pwReFilled = inputPwRe.text.toString().trim().isNotEmpty()
        val rrnFilled = inputRrn.text.toString().trim().isNotEmpty()
        val genderSelected = selectedGender != null

        if (idFilled && pwFilled && pwReFilled && rrnFilled && genderSelected) {
            nextButton.isEnabled = true
            nextButton.setBackgroundColor(Color.BLACK)
            nextButton.setTextColor(Color.WHITE)
        } else {
            nextButton.isEnabled = false
            nextButton.setBackgroundColor(Color.WHITE)
            nextButton.setTextColor(Color.BLACK)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun selectGender(gender: String) {
        selectedGender = gender
        applyGenderButtonStyle(gender)
        updateNextButtonState()
    }

    private fun applyGenderButtonStyle(gender: String?) {
        maleButton.backgroundTintList = null
        femaleButton.backgroundTintList = null

        if (gender == "남") {
            maleButton.setBackgroundResource(R.drawable.btn_gender_selected)
            maleButton.setTextColor(Color.WHITE)

            femaleButton.setBackgroundResource(R.drawable.btn_gender_unselected)
            femaleButton.setTextColor(Color.BLACK)
        } else if (gender == "여") {
            femaleButton.setBackgroundResource(R.drawable.btn_gender_selected)
            femaleButton.setTextColor(Color.WHITE)

            maleButton.setBackgroundResource(R.drawable.btn_gender_unselected)
            maleButton.setTextColor(Color.BLACK)
        } else {
            maleButton.setBackgroundResource(R.drawable.btn_gender_unselected)
            maleButton.setTextColor(Color.BLACK)

            femaleButton.setBackgroundResource(R.drawable.btn_gender_unselected)
            femaleButton.setTextColor(Color.BLACK)
        }
    }
}