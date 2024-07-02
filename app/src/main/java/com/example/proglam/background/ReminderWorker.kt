package com.example.proglam.background

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.proglam.MainActivity
import com.example.proglam.R
import com.example.proglam.utils.Notifications

class ReminderWorker(context : Context, params : WorkerParameters): Worker(context,params) {
    private lateinit var context: Context

    init {
        this.context = context
    }

    override fun doWork(): Result {
        sendPeriodicNotification()
        Log.i("MYTAG1","notificated")
        return Result.success()
    }

    private fun sendPeriodicNotification() {
        val intent: Intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, Notifications.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Activity tracker")
            .setContentText("It is time to register an activity")
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)

    }
}