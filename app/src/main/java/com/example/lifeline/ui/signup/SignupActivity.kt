package com.example.lifeline.ui.signup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lifeline.R
import androidx.fragment.app.Fragment
import com.example.lifeline.ui.signup.fragments.SignupStep1Fragment
import com.example.lifeline.ui.signup.fragments.SignupStep2Fragment
import com.example.lifeline.ui.signup.fragments.SignupStep3Fragment

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

        // step 1은 백스택에 쌓지 않음
        if (step != 1) {
            transaction.addToBackStack(null)
        }

        transaction.commit()
    }

}
