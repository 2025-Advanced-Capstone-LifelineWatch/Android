package com.example.lifeline.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.lifeline.R
import com.example.lifeline.ui.healthmanage.HealthManageActivity
import com.example.lifeline.ui.mypage.MyPageActivity
import com.example.lifeline.ui.chat.ChatListActivity

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val callButton = findViewById<Button>(R.id.btn_call)

        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val name = prefs.getString("name", "")
        val birthDate = prefs.getString("birthDate", "")
        val protectorName = prefs.getString("protectorName", "")
        val protectorContact = prefs.getString("protectorContact", "")
        val socialWorkerName = prefs.getString("socialWorkerName", "")
        val socialWorkerPhone = prefs.getString("socialWorkerPhone", "")


        callButton.setOnClickListener {
            if (!socialWorkerPhone.isNullOrBlank()) {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:$socialWorkerPhone")
                startActivity(intent)
            } else {
                Toast.makeText(this, "사회복지사 전화번호가 저장되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        }


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

        findViewById<Button>(R.id.btn_chat).setOnClickListener {
            startActivity(Intent(this, ChatListActivity::class.java))
        }
    }
}
