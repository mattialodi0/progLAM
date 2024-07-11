package com.example.proglam

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.example.proglam.utils.Notifications

class MainApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel1 = NotificationChannel(Notifications.CHANNEL_ID, "Activity Tracking", NotificationManager.IMPORTANCE_DEFAULT)
        channel1.description = "used for reminder notifications"

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel1)


        val channel2 = NotificationChannel(
            Notifications.ONGOING_CHANNEL_ID,
            "Ongoing Notification",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel2.description = "used for foreground services"
        notificationManager.createNotificationChannel(channel2)
    }
}