package com.example.lifeline.network

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://server.lifewatch.store/"
    private lateinit var context: Context

    // Application 클래스에서 context 초기화
    fun init(appContext: Context) {
        context = appContext.applicationContext
    }

    // OkHttpClient에 인터셉터 추가
    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .build()
    }

    // Retrofit 객체 생성
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client) // 👈 인터셉터 포함된 클라이언트 적용
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Auth API 서비스 인스턴스
    val authService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }
}
