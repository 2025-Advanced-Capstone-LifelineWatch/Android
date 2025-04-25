package com.example.lifeline.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import com.example.lifeline.R
import com.example.lifeline.ui.monitor.BloodPressureActivity

class HomeActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val btnBloodPressure: Button = findViewById(R.id.blood_pressure)
        btnBloodPressure.setOnClickListener {
            val intent = Intent(this, BloodPressureActivity::class.java)
            startActivity(intent)
        }
    }
}