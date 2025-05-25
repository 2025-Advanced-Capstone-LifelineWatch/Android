package com.example.lifeline.data.chat

data class ChatMessage(
    val message: String? = null,
    val senderName: String? = null,
    val createdAt: String,
    val isMe: Boolean = false,
    val profileImageUrl: String? = null,
    val isDateHeader: Boolean = false // ✅ 날짜 구분용
)

