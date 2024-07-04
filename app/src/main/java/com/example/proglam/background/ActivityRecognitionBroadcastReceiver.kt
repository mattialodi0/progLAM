package com.example.proglam.background

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity


class ActivityRecognitionBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("ttt","AAAAAAAAAA")
        Log.d("ttt",intent.toString())

        if (intent != null && ActivityRecognitionResult.hasResult(intent)) {
            val result = ActivityRecognitionResult.extractResult(intent)
            val mostProbableActivity = result?.mostProbableActivity

            val activityType = when (mostProbableActivity?.type) {
                DetectedActivity.IN_VEHICLE -> "In vehicle"
                DetectedActivity.ON_BICYCLE -> "On bicycle"
                DetectedActivity.ON_FOOT -> "On foot"
                DetectedActivity.RUNNING -> "Running"
                DetectedActivity.STILL -> "Still"
                DetectedActivity.TILTING -> "Tilting"
                DetectedActivity.WALKING -> "Walking"
                else -> "Unknown"
            }

            // Invia una notifica con il tipo di attività rilevata
            Log.d("ttt","Sending Notification")
            sendNotification(context, activityType)
        }
        else if(intent == null)
            Log.d("ttt","NULLLLLLLLLLL")
        else
            Log.d("ttt",ActivityRecognitionResult.extractResult(intent).toString())
    }

    private fun sendNotification(context: Context?, activityType: String) {
        // Codice per inviare una notifica
        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "ActivityChannel"
        val channelName = "Activity Channel"

            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)

        val notification = Notification.Builder(context, channelId)
            .setContentTitle("Attività rilevata")
            .setContentText("Nuova attività: $activityType")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        notificationManager.notify(0, notification)
    }
}
