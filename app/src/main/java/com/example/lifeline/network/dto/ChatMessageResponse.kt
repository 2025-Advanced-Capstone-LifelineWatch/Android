package com.example.lifeline.network.dto

data class ChatMessageResponse(
    val message: String,
    val senderId: Long,
    val roomId: Long,
    val senderName: String,
    val createdAt: String
)

data class ApiResponse<T>(
    val status: Status,
    val results: T
)

data class Status(
    val code: Int,
    val message: String
)