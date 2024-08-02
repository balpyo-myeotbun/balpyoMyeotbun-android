package com.project.balpyo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM Token", "onNewToken: ${token} ")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d("발표몇분", "onMessageReceived: ${message.from} ")
        Log.d("발표몇분", "onMessageReceived: ${message.data.toString()} ")
        Log.d("발표몇분", "onMessageReceived: ${message.data["scriptId"]} ")
        message.notification?.let {
            showNotification(messageTitle = it.title ?: "", messageBody = it.body ?: "", scriptId = message.data["scriptId"].toString() ?: "")
        }
    }



    private fun showNotification(messageTitle: String, messageBody: String, scriptId: String) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = (System.currentTimeMillis() / 7).toInt() // 고유 ID 지정

        createNotificationChannel(notificationManager)
        val intent = Intent(this, NotificationActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra("fragment", "DetailFragment")
            putExtra("scriptId", scriptId)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        notificationManager.notify(
            notificationID,
            notificationBuilder(messageTitle, messageBody, pendingIntent)
        )

    }

    private fun notificationBuilder(title: String, body: String, pendingIntent: PendingIntent) =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_icon_foreground))
            .setSmallIcon(R.mipmap.ic_icon_round)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()


    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableLights(true)
                enableVibration(true)
                description = CHANNEL_DESCRIPTION
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_NAME = "BalpyoNotification"
        private const val CHANNEL_DESCRIPTION = "Channel For Balpyo Notification"
        private const val CHANNEL_ID = "fcm_default_channel"
    }
}
