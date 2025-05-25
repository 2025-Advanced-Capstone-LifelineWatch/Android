package com.example.lifeline.network.dto

data class ChatRoomListResponse(
    val results: List<ChatRoomItem>
)

data class ChatRoomItem(
    val roomId: Long,
    val receiverId: Long,
    val receiverName: String,
    val createdAt: String,
    val lastMessage: String,
    val lastMessageAt: String?
)
