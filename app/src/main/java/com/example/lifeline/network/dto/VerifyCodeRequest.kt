package com.example.lifeline.network.dto

data class VerifyCodeRequest(
    val phoneNumber: String,
    val verificationCode: String
)
