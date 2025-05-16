package com.example.lifeline.data

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {

    companion object {
        private const val PREFS_NAME = "user_prefs"
        private const val KEY_TOKEN = "token"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_ID = "user_id"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // 🔐 JWT 토큰 저장/불러오기
    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    // 👤 사용자 이름 저장/불러오기
    fun saveUserName(name: String) {
        prefs.edit().putString(KEY_USER_NAME, name).apply()
    }

    fun getUserName(): String? = prefs.getString(KEY_USER_NAME, null)

    // 🆔 유저 ID 저장/불러오기
    fun saveUserId(userId: Int) {
        prefs.edit().putInt(KEY_USER_ID, userId).apply()
    }

    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, -1)

    // 🔄 전체 초기화
    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
