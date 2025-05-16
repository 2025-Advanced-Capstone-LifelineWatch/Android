package com.example.lifeline.ui.mypage.detailed.notice

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.lifeline.R

class NoticeDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_notice_detail)

        val title = intent.getStringExtra("title")
        val date = intent.getStringExtra("date")

        val titleTextView = findViewById<TextView>(R.id.detailTitle)
        val dateTextView = findViewById<TextView>(R.id.detailDate)
        val contentTextView = findViewById<TextView>(R.id.detailContent)

        titleTextView.text = title
        dateTextView.text = date
        contentTextView.text = getDummyContent(title ?: "")
    }

    private fun getDummyContent(title: String): String {
        return when {
            title.contains("위치정보") -> "위치정보 이용약관에 대한 개정 안내입니다...\n\n(예시 텍스트)"
            title.contains("개인정보처리방침") -> "개인정보처리방침이 아래와 같이 개정되었습니다...\n\n(예시 텍스트)"
            else -> "공지사항의 상세 내용입니다...\n\n(예시 텍스트)"
        }
    }
}
