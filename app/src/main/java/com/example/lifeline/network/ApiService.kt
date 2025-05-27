package com.example.lifeline.network

import com.example.lifeline.network.dto.AlarmGroupResponse
import com.example.lifeline.network.dto.ApiResponse
import com.example.lifeline.network.dto.ChatMessageResponse
import com.example.lifeline.network.dto.ChatRoomListResponse
import com.example.lifeline.network.dto.FcmTokenUpdate
import com.example.lifeline.network.dto.LoginRequest
import com.example.lifeline.network.dto.LoginResponse
import com.example.lifeline.network.dto.MyInfoUpdate
import com.example.lifeline.network.dto.PasswordUpdate
import com.example.lifeline.network.dto.RegisterMedicineRequest
import com.example.lifeline.network.dto.SignupRequest
import com.example.lifeline.network.dto.UpdateAlarmGroupRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import com.example.lifeline.network.dto.VerifyCodeRequest
import com.example.lifeline.network.dto.VerifyRequest
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {
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

    @GET("/api/chat-room/messages/{roomId}")
    suspend fun getChatMessages(
        @Path("roomId") roomId: Long,
        @Header("Authorization") authHeader: String
    ): Response<ApiResponse<List<ChatMessageResponse>>>

    @POST("api/auth/verify-identity")
    suspend fun verifyIdentify(
        @Body request: VerifyRequest
    ): Response<Void>

    @PATCH("api/user/me")
    suspend fun updateMyInfo(
        @Body request: MyInfoUpdate
    ): Response<Void>

    @PATCH("api/auth/password/change")
    suspend fun updatePassword(
        @Body request: PasswordUpdate
    ): Response<Void>

    @POST("api/alarm/group")
    suspend fun registerMedicineGroup(
        @Body request: RegisterMedicineRequest
    ): Response<Void>

    @GET("api/alarm/group")
    suspend fun getMedicineGroups(): Response<AlarmGroupResponse>

    @PUT("api/alarm/group/{groupId}/alarm")
    suspend fun updateAlarmGroup(
        @Path("groupId") groupId: Long,
        @Body request: UpdateAlarmGroupRequest
    ): Response<Unit>

}
