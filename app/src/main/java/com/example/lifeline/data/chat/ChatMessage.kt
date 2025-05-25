package com.example.lifeline.data.chat

data class ChatMessage(
    val message: String,
    val isMe: Boolean,
    val senderName: String,
    val createdAt: String,
    val profileImageUrl: String? = null
)

