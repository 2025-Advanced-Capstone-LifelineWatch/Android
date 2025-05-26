package com.example.lifeline.ui.mypage.detailed

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lifeline.R
import com.example.lifeline.network.RetrofitClient
import com.example.lifeline.network.dto.MyInfoUpdate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditProfileActivity : AppCompatActivity() {

    private lateinit var editAddress: EditText
    private lateinit var editPhone: EditText
    private lateinit var editGuardianName: EditText
    private lateinit var editGuardianPhone: EditText
    private lateinit var btnUpdate: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_edit_profile)

        initViews()
        loadUserInfoFromPrefs()
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)

        btnUpdate.setOnClickListener {
            val name = prefs.getString("name", "")
            val address = editAddress.text.toString()
            val phone = editPhone.text.toString()
            val guardianName = editGuardianName.text.toString()
            val guardianPhone = editGuardianPhone.text.toString()

            val request = name?.let {
                MyInfoUpdate(
                    name = it,
                    address = address,
                    phoneNumber = phone,
                    protectorName = guardianName,
                    protectorContact = guardianPhone
                )
            }

            btnUpdate.isEnabled = false

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = request?.let { RetrofitClient.apiService.updateMyInfo(it) }
                    withContext(Dispatchers.Main) {
                        if (response != null && response.isSuccessful) {
                            saveUserInfoToPrefs(address, phone, guardianName, guardianPhone)
                            Toast.makeText(
                                this@EditProfileActivity,
                                "정보가 수정되었습니다",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(this@EditProfileActivity, "정보 수정 실패", Toast.LENGTH_SHORT)
                                .show()
                        }
                        btnUpdate.isEnabled = true
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@EditProfileActivity,
                            "오류 발생: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        btnUpdate.isEnabled = true
                    }
                }
            }
        }
    }

        private fun initViews() {
        editAddress = findViewById(R.id.edit_address)
        editPhone = findViewById(R.id.edit_phone)
        editGuardianName = findViewById(R.id.edit_guardian_name)
        editGuardianPhone = findViewById(R.id.edit_guardian_phone)
        btnUpdate = findViewById(R.id.btn_update)
    }

    private fun loadUserInfoFromPrefs() {
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        editAddress.setText(prefs.getString("address", ""))
        editPhone.setText(prefs.getString("phoneNumber", ""))
        editGuardianName.setText(prefs.getString("protectorName", ""))
        editGuardianPhone.setText(prefs.getString("protectorContact", ""))
    }

    private fun saveUserInfoToPrefs(
        address: String,
        phone: String,
        guardianName: String,
        guardianPhone: String
    ) {
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        with(prefs.edit()) {
            putString("address", address)
            putString("phoneNumber", phone)
            putString("protectorName", guardianName)
            putString("protectorContact", guardianPhone)
            apply()
        }
    }
}
