package com.example.lifeline.network.dto

data class VerifyRequest(
    val loginId: String,
    val phoneNumber: String
)
