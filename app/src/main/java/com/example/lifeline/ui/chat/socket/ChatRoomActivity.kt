package com.example.lifeline.ui.chat.socket

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lifeline.R
import com.example.lifeline.data.chat.ChatMessage
import com.example.lifeline.network.RetrofitClient
import com.example.lifeline.network.dto.ChatMessageResponse
import kotlinx.coroutines.launch
import ua.naiksoftware.stomp.dto.StompHeader
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class ChatRoomActivity : AppCompatActivity() {
    private lateinit var adapter: ChatMessageAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: ImageButton
    private lateinit var tvReceiverName: TextView
    private lateinit var socketManager: ChatSocketManager

    private var roomId: Long = -1L
    private var userId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_chat_room)

        roomId = intent.getLongExtra("roomId", -1L)
        val receiverName = intent.getStringExtra("receiverName") ?: "알 수 없음"
        userId = getCurrentUserId()

        if (roomId == -1L || userId == -1L) {
            finish()
            return
        }

        tvReceiverName = findViewById(R.id.tv_receiver_name)
        tvReceiverName.text = receiverName

        adapter = ChatMessageAdapter(mutableListOf())
        recyclerView = findViewById(R.id.rv_messages)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        etMessage = findViewById(R.id.et_message)
        btnSend = findViewById(R.id.btn_send)

        socketManager = ChatSocketManager(adapter, roomId, userId) {
            recyclerView.scrollToPosition(adapter.itemCount - 1)
        }
        socketManager.connect(getToken())


        loadPreviousMessages()

        btnSend.setOnClickListener {
            val text = etMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                socketManager.sendMessage(userId, text)
                val now = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                adapter.addMessage(
                    ChatMessage(
                        message = text,
                        isMe = true,
                        senderName = getCurrentUserName(),
                        createdAt = now
                    )
                )
                recyclerView.scrollToPosition(adapter.itemCount - 1)
                etMessage.text.clear()
            }
        }
    }

    override fun onDestroy() {
        socketManager.disconnect()
        super.onDestroy()
    }

    private fun loadPreviousMessages() {
        val token = getToken()
        Log.d("STOMP", "🪪 JWT Token = Bearer $token")

        val apiService = RetrofitClient.apiService

        lifecycleScope.launch {
            try {
                val response = apiService.getChatMessages(roomId, "Bearer $token")
                if (response.isSuccessful) {
                    val messages = response.body()?.results ?: emptyList()
                    adapter.setMessages(messages.map {
                        ChatMessage(
                            message = it.message,
                            isMe = it.senderId == userId,
                            senderName = it.senderName,
                            createdAt = it.createdAt,
                        )
                    })
                    recyclerView.scrollToPosition(adapter.itemCount - 1)
                } else {
                    Log.e("ChatRoom", "❌ 메시지 불러오기 실패 - ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("ChatRoom", "❌ 메시지 불러오기 실패", e)
            }
        }
    }

    private fun getCurrentUserId(): Long {
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        return prefs.getInt("userId", -1).toLong()
    }

    private fun getCurrentUserName(): String {
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        return prefs.getString("name", "나") ?: "나"
    }

    private fun getToken(): String {
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        return prefs.getString("token", "") ?: ""
    }
}