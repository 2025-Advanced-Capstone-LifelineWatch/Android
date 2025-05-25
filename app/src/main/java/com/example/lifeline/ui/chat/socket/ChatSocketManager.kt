package com.example.lifeline.ui.chat.socket

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.lifeline.data.chat.ChatMessage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.json.JSONObject
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompHeader

class ChatSocketManager(
    private val adapter: ChatMessageAdapter,
    private val roomId: Long,
    private val currentUserId: Long
) {
    private lateinit var stompClient: StompClient

    private var messageSubscription: Disposable? = null
    private var lifecycleSubscription: Disposable? = null

    private val TAG = "STOMP"

    fun connect(token: String) {
        val url = "wss://server.lifewatch.store/ws-chat"
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url)
        val headers = listOf(StompHeader("Authorization", "Bearer $token"))

        stompClient.connect(headers)

        lifecycleSubscription = stompClient.lifecycle()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ event ->
                when (event.type) {
                    LifecycleEvent.Type.OPENED -> {
                        Log.d(TAG, "✅ WebSocket 연결됨 (OPENED)")

                        // STOMP CONNECTED 직후를 위한 안전한 지연
                        Handler(Looper.getMainLooper()).postDelayed({
                            if (stompClient.isConnected) {
                                Log.d(TAG, "📡 STOMP 연결 완료 → 구독 시작")
                                subscribeToRoom()
                            } else {
                                Log.w(TAG, "❌ stompClient.isConnected == false (구독 생략)")
                            }
                        }, 300) // 300~500ms 지연 (서버와 환경에 따라 조절 가능)
                    }

                    LifecycleEvent.Type.CLOSED -> {
                        Log.w(TAG, "⚠️ 연결 종료됨")
                    }

                    LifecycleEvent.Type.ERROR -> {
                        Log.e(TAG, "❌ 연결 오류 발생", event.exception)
                    }

                    LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> {
                        Log.w(TAG, "❗ 서버 heartbeat 실패")
                    }
                }
            }, { error ->
                Log.e(TAG, "❗ lifecycle 처리 실패", error)
            })
    }

    private fun subscribeToRoom() {
        val topic = "/exchange/chat.exchange/room.$roomId"
        Log.d(TAG, "📡 구독 시도 → $topic")

        messageSubscription = stompClient.topic(topic)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ topicMessage ->
                Log.d(TAG, "📥 수신된 메시지 = ${topicMessage.payload}")
                try {
                    val data = JSONObject(topicMessage.payload)
                    val message = ChatMessage(
                        message = data.getString("message"),
                        senderName = data.getString("senderName"),
                        createdAt = data.getString("createdAt"),
                        isMe = data.getLong("senderId") == currentUserId,
                        profileImageUrl = data.optString("profileImageUrl", null)
                    )
                    adapter.addMessage(message)
                } catch (e: Exception) {
                    Log.e(TAG, "📦 JSON 파싱 실패", e)
                }
            }, { error ->
                Log.e(TAG, "❌ 메시지 구독 실패", error)
            })
    }

    fun sendMessage(senderId: Long, messageText: String) {
        if (!stompClient.isConnected) {
            Log.w(TAG, "⚠️ 메시지 전송 시도했지만 연결되지 않음")
            return
        }

        val json = JSONObject().apply {
            put("senderId", senderId)
            put("roomId", roomId)
            put("message", messageText)
        }

        stompClient.send("/pub/chat.message", json.toString())
            .subscribe({
                Log.d(TAG, "✅ 메시지 전송 성공")
            }, { error ->
                Log.e(TAG, "❌ 메시지 전송 실패", error)
            })
    }

    fun disconnect() {
        Log.d(TAG, "🧹 연결 종료 및 리소스 정리")
        messageSubscription?.dispose()
        lifecycleSubscription?.dispose()
        if (::stompClient.isInitialized) {
            stompClient.disconnect()
        }
    }
}
