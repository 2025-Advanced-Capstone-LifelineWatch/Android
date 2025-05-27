package com.example.lifeline.ui.chat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lifeline.R
import com.example.lifeline.network.RetrofitClient
import com.example.lifeline.ui.chat.socket.ChatRoomActivity
import kotlinx.coroutines.launch

class ChatListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var chatRoomAdapter: ChatRoomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_chat_list)

        recyclerView = findViewById(R.id.rv_chat_list)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchChatList()
    }

    override fun onResume() {
        super.onResume()
        fetchChatList()
    }

    private fun fetchChatList() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.authService.getChatRooms()
                if (response.isSuccessful) {
                    val chatList = response.body()?.results ?: emptyList()
                    chatRoomAdapter = ChatRoomAdapter(chatList) { chatRoomItem ->
                        val intent = Intent(this@ChatListActivity, ChatRoomActivity::class.java).apply {
                            putExtra("roomId", chatRoomItem.roomId)
                            putExtra("receiverName", chatRoomItem.receiverName)
                        }
                        startActivity(intent)
                    }
                    recyclerView.adapter = chatRoomAdapter
                } else {
                    Log.e("ChatList", "서버 응답 실패: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("ChatList", "예외 발생", e)
            }
        }
    }

}
