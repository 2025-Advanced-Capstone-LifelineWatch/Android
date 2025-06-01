package com.example.lifeline.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.lifeline.R
import com.example.lifeline.ui.main.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class LifelineFirebaseService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val title = remoteMessage.data["title"] ?: "라이프라인 알림"
        val body = remoteMessage.data["body"] ?: ""

        val formattedBody = formatAlarmBody(body)
        sendCustomNotification(title, formattedBody)
    }

    private fun formatAlarmBody(rawBody: String): String {
        val timeRegex = Regex("""복용 시간: (\d{4}-\d{2}-\d{2}T\d{2}:\d{2})""")
        val match = timeRegex.find(rawBody)

        return if (match != null) {
            val originalTime = match.groupValues[1]
            try {
                val parsedTime = LocalDateTime.parse(originalTime)
                val formatter = DateTimeFormatter.ofPattern("MM-dd, a hh:mm", Locale.KOREA)
                val formattedTime = parsedTime.format(formatter)
                rawBody.replace(originalTime, formattedTime)
            } catch (e: Exception) {
                rawBody
            }
        } else {
            rawBody
        }
    }

    private fun sendCustomNotification(title: String, body: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "lifeline_channel"

        val remoteViews = RemoteViews(packageName, R.layout.custom_notification).apply {
            setTextViewText(R.id.tvMedicineName, title)
            setTextViewText(R.id.tvDosage, body)
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(remoteViews)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Lifeline 알림 채널",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = title.hashCode()
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

}
