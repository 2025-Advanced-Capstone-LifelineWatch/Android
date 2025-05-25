package com.example.lifeline.ui.signup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lifeline.R
import androidx.fragment.app.Fragment
import com.example.lifeline.ui.signup.fragments.SignupStep1Fragment
import com.example.lifeline.ui.signup.fragments.SignupStep2Fragment
import com.example.lifeline.ui.signup.fragments.SignupStep3Fragment

val userSignupData = SignupData()

data class SignupData(
    var name: String = "",
    var loginId: String = "",
    var email: String = "",
    var password: String = "",
    var phoneNumber: String = "",
    var address: String = "",
    var rrn: String = "",
    var drn: String = "",
    var socialWorkerId: Long = 0,
    var birthDate: String = "",
    var gender: String = "",
    var protectorContact: String = "",
    var protectorName: String = "",
    var verificationCode: String = "",
    val fcmToken: String = ""
)

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_signup)

        if (savedInstanceState == null) {
            goToStep(1)
        }
    }

    fun goToStep(step: Int) {
        val fragment: Fragment = when (step) {
            1 -> SignupStep1Fragment()
            2 -> SignupStep2Fragment()
            3 -> SignupStep3Fragment()
            else -> return
        }

        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)

        if (step != 1) {
            transaction.addToBackStack(null)
        }

        transaction.commit()
    }

}
