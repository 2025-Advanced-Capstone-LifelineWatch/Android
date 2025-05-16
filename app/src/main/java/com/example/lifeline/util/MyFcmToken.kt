package com.example.lifeline.util

import android.app.Application
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging

class MyFcmToken : Application() {
    companion object {
        var fcmToken: String? =null
    }
    override fun onCreate() {
        super.onCreate()

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                fcmToken = task.result
                Log.d("FCMToken", "Token: $fcmToken")
            } else {
                Log.w("FCMToken", "토큰 가져오기 실패", task.exception)
            }
        }
    }
}