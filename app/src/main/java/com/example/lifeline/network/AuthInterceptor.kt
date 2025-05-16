package com.example.lifeline.network

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("token", null)

        val originalRequest = chain.request()

        val modifiedRequest = if (!token.isNullOrBlank()) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(modifiedRequest)
    }
}
