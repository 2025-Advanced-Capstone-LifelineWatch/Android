package com.example.lifeline.ui.mypage

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lifeline.R

class MyPageActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyPageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_my_page)

        recyclerView = findViewById(R.id.recyclerView)
        adapter = MyPageAdapter(getItems()) { item ->
            Toast.makeText(this, "${item.text} 클릭됨", Toast.LENGTH_SHORT).show()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun getItems(): List<MyPageItem> {
        return listOf(
            MyPageItem(null, "개인정보 수정", "내정보관리"),
            MyPageItem(null, "비밀번호 변경"),
            MyPageItem(null, "새로운 소식 확인", "공지사항"),
            MyPageItem(null, "약관 내용 확인", "서비스 이용 약관"),
            MyPageItem(null, "정보 처리 방침 확인", "개인정보 수집 및 이용")
        )
    }
}