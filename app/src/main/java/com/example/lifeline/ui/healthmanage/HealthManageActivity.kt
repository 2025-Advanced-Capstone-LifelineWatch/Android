package com.example.lifeline.ui.healthmanage

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.lifeline.R
import com.example.lifeline.ui.monitor.BloodPressureActivity
import com.example.lifeline.ui.monitor.CaloriesActivity
import com.example.lifeline.ui.monitor.OxygenSaturationActivity
import com.example.lifeline.ui.monitor.RespirationActivity
import com.example.lifeline.ui.monitor.StepsActivity
import com.example.lifeline.ui.monitor.TemperatureActivity


class HealthManageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_health_manage)

        val cardBlood = findViewById<CardView>(R.id.card_blood_pressure)
        val cardTemp = findViewById<CardView>(R.id.card_temperature)
        val cardBreath = findViewById<CardView>(R.id.card_respiration)
        val cardSteps = findViewById<CardView>(R.id.card_steps)
        val cardCalories = findViewById<CardView>(R.id.card_calories)
        val cardOxygen = findViewById<CardView>(R.id.card_oxy)

        cardBlood.setOnClickListener {
            startActivity(Intent(this, BloodPressureActivity::class.java))
        }

        cardTemp.setOnClickListener {
            startActivity(Intent(this, TemperatureActivity::class.java))
        }

        cardBreath.setOnClickListener {
            startActivity(Intent(this, RespirationActivity::class.java))
        }

        cardOxygen.setOnClickListener {
            startActivity(Intent(this, OxygenSaturationActivity::class.java))
        }

        cardSteps.setOnClickListener {
            startActivity(Intent(this, StepsActivity::class.java))
        }

        cardCalories.setOnClickListener {
            startActivity(Intent(this, CaloriesActivity::class.java))
        }
    }
}
