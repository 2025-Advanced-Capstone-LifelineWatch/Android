package com.example.lifeline.ui.chat

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lifeline.R
import ua.naiksoftware.stomp.dto.StompHeader

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

        tvReceiverName = findViewById(R.id.tv_receiver_name)
        tvReceiverName.text = receiverName

        adapter = ChatMessageAdapter(mutableListOf())
        recyclerView = findViewById(R.id.rv_messages)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        etMessage = findViewById(R.id.et_message)
        btnSend = findViewById(R.id.btn_send)

        socketManager = ChatSocketManager(this, adapter, roomId, userId)
        socketManager.connect(
            listOf(
                StompHeader("Authorization", "Bearer ${getToken()}")
            )
        )

        btnSend.setOnClickListener {
            val text = etMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                socketManager.sendMessage(userId, text)
                etMessage.text.clear()
            }
        }
    }

    override fun onDestroy() {
        socketManager.disconnect()
        super.onDestroy()
    }

    private fun getCurrentUserId(): Long {
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        return prefs.getLong("userId", -1L)
    }

    private fun getToken(): String {
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        return prefs.getString("token", "") ?: ""
    }

}
