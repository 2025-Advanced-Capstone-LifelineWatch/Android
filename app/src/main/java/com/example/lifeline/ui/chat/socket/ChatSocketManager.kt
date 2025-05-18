package com.example.lifeline.ui.chat

import android.content.Context
import android.util.Log
import com.example.lifeline.data.chat.ChatMessage
import io.reactivex.android.schedulers.AndroidSchedulers
import org.json.JSONObject
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.StompHeader

class ChatSocketManager(
    private val adapter: ChatMessageAdapter,
    private val roomId: Long,
    private val currentUserId: Long
) {

    private lateinit var stompClient: StompClient

    fun connect(headers: List<StompHeader>) {
        val url = "ws://server.lifewatch.store/:8080/chat" // 서버 주소에 맞게 수정하세요
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url)

        stompClient.connect(headers)

        stompClient.lifecycle()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { lifecycleEvent ->
                when (lifecycleEvent.type) {
                    LifecycleEvent.Type.OPENED -> Log.d("STOMP", "WebSocket 연결됨")
                    LifecycleEvent.Type.ERROR -> Log.e("STOMP", " 연결 오류", lifecycleEvent.exception)
                    LifecycleEvent.Type.CLOSED -> Log.d("STOMP", " 연결 종료")
                    else -> {}
                }
            }

        subscribeToRoom()
    }

    private fun subscribeToRoom() {
        stompClient.topic("/exchange/chat.exchange/room.$roomId")
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ topicMessage ->
                val data = JSONObject(topicMessage.payload)
                val message = data.getString("message")
                val senderId = data.getLong("senderId")
                val senderName = data.getString("senderName")
                val createdAt = data.getString("createdAt")
                val profileImageUrl = data.optString("profileImageUrl", null)

                adapter.addMessage(
                    ChatMessage(
                        message = message,
                        isMe = senderId == currentUserId,
                        senderName = senderName,
                        createdAt = createdAt,
                        profileImageUrl = profileImageUrl
                    )
                )
            }, { error ->
                Log.e("STOMP", "메시지 구독 실패", error)
            })
    }

    fun sendMessage(userId: Long, messageText: String) {
        val messageJson = JSONObject().apply {
            put("userId", userId)
            put("roomId", roomId)
            put("message", messageText)
        }
        stompClient.send("/pub/message", messageJson.toString()).subscribe()
    }

    fun disconnect() {
        if (::stompClient.isInitialized) {
            stompClient.disconnect()
        }
    }
}
