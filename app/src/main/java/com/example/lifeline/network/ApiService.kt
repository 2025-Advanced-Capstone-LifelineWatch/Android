package com.example.lifeline.network

import com.example.lifeline.network.dto.ChatRoomListResponse
import com.example.lifeline.network.dto.FcmTokenUpdate
import com.example.lifeline.network.dto.LoginRequest
import com.example.lifeline.network.dto.LoginResponse
import com.example.lifeline.network.dto.SignupRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import com.example.lifeline.network.dto.VerifyCodeRequest
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Query


interface AuthApiService {
    @GET("api/auth/sms")
    suspend fun requestSmsCode(
        @Query("phone") phoneNumber: String
    ): Response<Unit>

    @POST("api/auth/sms/verify")
    suspend fun verifySmsCode(
        @Body request: VerifyCodeRequest
    ): Response<Unit>

    @POST("api/auth/signup")
    suspend fun signup(
        @Body request: SignupRequest,
    ): Response<Unit>

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @PATCH("api/auth/fcm-token")
    suspend fun updateFcmToken(
        @Body request: FcmTokenUpdate
    ): Response<Unit>

    @GET("/api/chat-room/list")
    suspend fun getChatRooms(): Response<ChatRoomListResponse>
}
