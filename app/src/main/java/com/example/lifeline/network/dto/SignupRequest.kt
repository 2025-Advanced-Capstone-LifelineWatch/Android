package com.example.lifeline.network.dto

data class SignupRequest(
    val name: String,
    val loginId: String,
    val email: String?,
    val password: String,
    val phoneNumber: String,
    val address: String,
    val rrn: String,
    val drn: String,
    val socialWorkerId: Long,
    val birthDate: String,
    val gender: String,
    val protectorContact: String,
    val protectorName: String,
    val verificationCode: String,
    val fcmToken: String
)
