/*
package com.example.lifeline.service

import android.app.*
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.lifeline.util.HealthConnectManager
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.time.Instant
import java.time.ZonedDateTime

class HealthDataService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var manager: HealthConnectManager
    private val client = OkHttpClient()

    override fun onCreate() {
        super.onCreate()
        manager = HealthConnectManager(applicationContext)
        startForeground(1, createNotification())
        startSendingLoop()
    }

    private fun createNotification(): Notification {
        val channelId = "health_data_channel"
        val channel = NotificationChannel(channelId, "Health Data Service", NotificationManager.IMPORTANCE_LOW)
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)

        return Notification.Builder(this, channelId)
            .setContentTitle("헬스 데이터 전송 중")
            .setContentText("30초마다 서버로 헬스 데이터를 전송합니다.")
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .build()
    }

    private var lastSentTime: Instant? = null

    private fun startSendingLoop() {
        serviceScope.launch {
            var retryDelay = 30_000L

            while (isActive) {
                try {
                    val now = ZonedDateTime.now().toInstant()
                    val startTime = lastSentTime ?: now.minusSeconds(30)
                    val endTime = now
                    lastSentTime = endTime

                    val heartRate = manager.readHeartRate(startTime, endTime).lastOrNull()?.samples?.lastOrNull()?.beatsPerMinute
                    val breathRate = manager.readRespiratoryRate(startTime, endTime).lastOrNull()?.rate
                    val spo2 = manager.readOxygenSaturation(startTime, endTime).lastOrNull()?.percentage

                    val dailyStart = ZonedDateTime.now().toLocalDate().atStartOfDay(ZonedDateTime.now().zone).toInstant()
                    val steps = manager.readSteps(dailyStart, endTime).sumOf { it.count.toInt() }
                    val calories = manager.readCalories(dailyStart, endTime).sumOf { it.energy.inKilocalories.toInt() }

                    val payload = JSONObject().apply {
                        put("Heartrate", heartRate)
                        put("Breathrate", breathRate)
                        put("SPO2", spo2)
                        put("Walking_steps", steps)
                        put("Caloricexpenditure", calories)
                    }

                    val request = Request.Builder()
                        .url("http://192.168.0.2:8005/predict")
                        .post(payload.toString().toRequestBody("application/json".toMediaType()))
                        .build()

                    val response = client.newCall(request).execute()
                    if (!response.isSuccessful) throw Exception("Server error: ${response.code}")

                    Log.i("HealthDataService", "데이터 전송 성공: $payload")
                    retryDelay = 30_000L

                } catch (e: Exception) {
                    Log.e("HealthDataService", "예외 발생: ${e.message}")
                    retryDelay = (retryDelay * 1.5).coerceAtMost((5 * 60 * 1000L).toDouble()).toLong()
                }

                delay(retryDelay)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
*/

package com.example.lifeline.service

import android.app.*
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.lifeline.util.HealthConnectManager
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class HealthDataService : Service() {

    private val TAG = "HealthDataService"
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var manager: HealthConnectManager
    private val client = OkHttpClient()

    private val SEND_INTERVAL = 30 * 1000L

    override fun onCreate() {
        super.onCreate()
        manager = HealthConnectManager(applicationContext)
        startForeground(1, createNotification())
        startSendingLoop()
    }

    private fun createNotification(): Notification {
        val channelId = "health_data_channel"
        val channel = NotificationChannel(channelId, "Health Data Service", NotificationManager.IMPORTANCE_LOW)
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)

        return Notification.Builder(this, channelId)
            .setContentTitle("헬스 데이터 전송 중")
            .setContentText("30초마다 서버로 헬스 데이터를 전송합니다.")
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .build()
    }

    private var lastSentTime: Instant? = null

    private fun startSendingLoop() {
        serviceScope.launch {
            var retryDelay = SEND_INTERVAL

            while (isActive) {
                try {
                    val now = ZonedDateTime.now().toInstant()

                    // 마지막 전송 시간이 없거나 30초가 지났으면 데이터 전송
                    if (lastSentTime == null || ChronoUnit.MILLIS.between(lastSentTime, now) >= SEND_INTERVAL) {
                        sendLatestHealthData()
                        lastSentTime = now
                        retryDelay = SEND_INTERVAL // 성공 시 기본 간격으로 리셋
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "데이터 전송 중 예외 발생: ${e.message}")
                    // 실패 시 지수 백오프 적용 (최대 3시간)
                    retryDelay = (retryDelay * 1.5).coerceAtMost(3 * 60 * 60 * 1000.0).toLong()
                }

                delay(retryDelay)
            }
        }
    }

    private suspend fun sendLatestHealthData() {
        val now = ZonedDateTime.now().toInstant()
        // 최근 30초 내의 데이터 조회
        val startTime = now.minus(30, ChronoUnit.SECONDS)
        val dailyStart = ZonedDateTime.now().toLocalDate().atStartOfDay(ZonedDateTime.now().zone).toInstant()
        // 가장 최근의 건강 데이터 가져오기
        val heartRate = manager.readHeartRate(dailyStart, now).firstOrNull()?.samples?.lastOrNull()?.beatsPerMinute
        val breathRate = manager.readRespiratoryRate(dailyStart, now).firstOrNull()?.rate
        val spo2 = manager.readOxygenSaturation(dailyStart, now).firstOrNull()?.percentage

        // 당일 누적 데이터
        val steps = manager.readSteps(dailyStart, now).sumOf { it.count.toInt() }
        val calories = manager.readCalories(dailyStart, now).sumOf { it.energy.inKilocalories.toInt() }

        // 누락된 데이터 처리
        if (heartRate == null && breathRate == null && spo2 == null && steps == 0 && calories == 0) {
            Log.i(TAG, "전송할 건강 데이터가 없습니다.")
            return
        }

        val payload = JSONObject().apply {
            put("Heartrate", heartRate ?: JSONObject.NULL)
            put("Breathrate", breathRate ?: JSONObject.NULL)
            put("SPO2", spo2 ?: JSONObject.NULL)
            put("Walking_steps", steps)
            put("Caloricexpenditure", calories)
        }

        Log.d(TAG, "서버로 전송할 데이터: $payload")

        val request = Request.Builder()
            .url("http://192.168.0.2:8005/predict")
            .post(payload.toString().toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("서버 오류: ${response.code}")
            }
            Log.i(TAG, "데이터 전송 성공")
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}