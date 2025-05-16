package com.example.lifeline.network


import com.example.lifeline.network.dto.SignupRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import com.example.lifeline.network.dto.SmsRequest
import com.example.lifeline.network.dto.VerifyCodeRequest
import retrofit2.http.GET
import retrofit2.http.Header
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
        @Header("fcm_token") fcmToken: String
    ): Response<Unit>
}
