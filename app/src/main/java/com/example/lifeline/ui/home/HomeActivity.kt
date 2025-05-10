package com.example.lifeline.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import com.example.lifeline.R
import com.example.lifeline.ui.monitor.BloodPressureActivity
import com.example.lifeline.ui.mypage.MyPageActivity


class HomeActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val btnHealthCheck: Button = findViewById(R.id.health_check)
        btnHealthCheck.setOnClickListener {
            val intent = Intent(this, BloodPressureActivity::class.java)
            startActivity(intent)
        }

        val btnMyPage: Button = findViewById(R.id.my_page)
        btnMyPage.setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
        }
    }
}