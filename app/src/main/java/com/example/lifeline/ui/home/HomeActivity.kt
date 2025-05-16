package com.example.lifeline.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.lifeline.R
import com.example.lifeline.ui.healthmanage.HealthManageActivity
import com.example.lifeline.ui.mypage.MyPageActivity

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val name = prefs.getString("name", "")
        val birthDate = prefs.getString("birthDate", "")
        val protectorName = prefs.getString("protectorName", "")
        val protectorContact = prefs.getString("protectorContact", "")
        val socialWorkerName = prefs.getString("socialWorkerName", "")
        val socialWorkerPhone = prefs.getString("socialWorkerPhone", "")

        findViewById<TextView>(R.id.tv_user_name).text = name
        findViewById<TextView>(R.id.my_birth).text = birthDate
        findViewById<TextView>(R.id.protector_name).text = protectorName
        findViewById<TextView>(R.id.protector_phone).text = protectorContact
        findViewById<TextView>(R.id.social_worker_name).text = socialWorkerName
        findViewById<TextView>(R.id.social_worker_phone).text = socialWorkerPhone

        findViewById<Button>(R.id.health_check).setOnClickListener {
            startActivity(Intent(this, HealthManageActivity::class.java))
        }

        findViewById<Button>(R.id.my_page).setOnClickListener {
            startActivity(Intent(this, MyPageActivity::class.java))
        }
    }
}
