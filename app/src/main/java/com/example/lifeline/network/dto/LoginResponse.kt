package com.example.lifeline.network.dto

data class LoginResponse(
    val results: List<UserInfo>
)

data class UserInfo(
    val name: String,
    val token: String,
    val birthDate: String,
    val protectorName: String,
    val protectorContact: String,
    val socialWorkerName: String,
    val socialWorkerPhone: String,
    val userId: Int,
    val isSocialWorker: Boolean
)
