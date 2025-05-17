package com.example.lifeline.ui.mypage.detailed.notice

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lifeline.R
import com.example.lifeline.data.mypage.NoticeItem

class NoticeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var noticeAdapter: NoticeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_notice)

        recyclerView = findViewById(R.id.recyclerViewNotice)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val noticeList = getNoticeItems()
        noticeAdapter = NoticeAdapter(noticeList)
        recyclerView.adapter = noticeAdapter
    }

    private fun getNoticeItems(): List<NoticeItem> {
        return listOf(
            NoticeItem("위치정보 이용약관 안내", "2025-05-17"),
            NoticeItem("개인위치정보처리방침 안내", "2025-05-17"),
            NoticeItem("개인정보처리방침 안내", "2025-05-17"),
        )
    }
}
