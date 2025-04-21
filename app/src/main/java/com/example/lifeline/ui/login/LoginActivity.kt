package com.example.lifeline.presentation.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import com.example.lifeline.R
import com.example.lifeline.ui.home.HomeActivity

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        val loginButton: Button = findViewById(R.id.btn_login)

        loginButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }


    }

}
