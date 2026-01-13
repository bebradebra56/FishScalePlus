package com.fishscal.plisfo.trng.presentation.notificiation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.fishscal.plisfo.FishScalePlusActivity
import com.fishscal.plisfo.R
import com.fishscal.plisfo.trng.presentation.app.FishScalePlusApplication
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

private const val FISH_SCALE_PLUS_CHANNEL_ID = "fish_scale_plus_notifications"
private const val FISH_SCALE_PLUS_CHANNEL_NAME = "FishScalePlus Notifications"
private const val FISH_SCALE_PLUS_NOT_TAG = "FishScalePlus"

class FishScalePlusPushService : FirebaseMessagingService(){
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Обработка notification payload
        remoteMessage.notification?.let {
            if (remoteMessage.data.contains("url")) {
                fishScalePlusShowNotification(it.title ?: FISH_SCALE_PLUS_NOT_TAG, it.body ?: "", data = remoteMessage.data["url"])
            } else {
                fishScalePlusShowNotification(it.title ?: FISH_SCALE_PLUS_NOT_TAG, it.body ?: "", data = null)
            }
        }

        // Обработка data payload
        if (remoteMessage.data.isNotEmpty()) {
            fishScalePlusHandleDataPayload(remoteMessage.data)
        }
    }

    private fun fishScalePlusShowNotification(title: String, message: String, data: String?) {
        val fishScalePlusNotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Создаем канал уведомлений для Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                FISH_SCALE_PLUS_CHANNEL_ID,
                FISH_SCALE_PLUS_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            fishScalePlusNotificationManager.createNotificationChannel(channel)
        }

        val fishScalePlusIntent = Intent(this, FishScalePlusActivity::class.java).apply {
            putExtras(bundleOf(
                "url" to data
            ))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val fishScalePlusPendingIntent = PendingIntent.getActivity(
            this,
            0,
            fishScalePlusIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val fishScalePlusNotification = NotificationCompat.Builder(this, FISH_SCALE_PLUS_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.fish_scale_plus_noti_ic)
            .setAutoCancel(true)
            .setContentIntent(fishScalePlusPendingIntent)
            .build()

        fishScalePlusNotificationManager.notify(System.currentTimeMillis().toInt(), fishScalePlusNotification)
    }

    private fun fishScalePlusHandleDataPayload(data: Map<String, String>) {
        data.forEach { (key, value) ->
            Log.d(FishScalePlusApplication.FISH_SCALE_PLUS_MAIN_TAG, "Data key=$key value=$value")
        }
    }
}