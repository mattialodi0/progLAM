package com.example.proglam.background

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import com.example.proglam.R
import com.example.proglam.db.ActivityDatabase
import com.example.proglam.db.ActivityRecord
import com.example.proglam.utils.ActivityTransitionsUtil
import com.google.android.gms.location.ActivityTransitionResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ActivityRecognitionBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {

        if (ActivityTransitionResult.hasResult(intent)) {
            val result = intent?.let { ActivityTransitionResult.extractResult(it) }
            result?.let {
                result.transitionEvents.forEach { event ->

                    val activityType = ActivityTransitionsUtil.toActivityString(event.activityType)
                    val transitionType =
                        ActivityTransitionsUtil.toTransitionType(event.transitionType)
                    val info =
                        "Transition: " + activityType +
                                " (" + transitionType + ")" + "   " +
                                SimpleDateFormat("HH:mm:ss", Locale.ITALY).format(Date())

                    Log.i("ActivityRecognitionBroadcastReceiver", info)

                    if (transitionType == "ENTER") {
                        sendNotification(context, activityType, 0)
                    } else if (transitionType == "EXIT") {
                        val startTime = System.currentTimeMillis() - ((SystemClock.elapsedRealtimeNanos() - event.elapsedRealTimeNanos) / 1000000)
                        val finishTime = System.currentTimeMillis()

                        if (finishTime - startTime > 5000) {    // better 60000
                            val newActivityRecord = ActivityRecord(
                                0,
                                activityType,
                                startTime,
                                finishTime,
                                "{}"
                            )
                            saveActivityRecord(context, newActivityRecord)
                            sendNotification(context, activityType, 1)
                        }
                    }
                }
            }
        }
    }

    private fun sendNotification(context: Context?, activityType: String, transition: Int) {
        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "ActivityChannel"
        val channelName = "Activity Channel"
        val title = if (transition == 0) "Activity started" else "Activity recorded"

        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        val notification = Notification.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText("type: $activityType")
            .setSmallIcon(R.drawable.ic_activitytype_generic)
            .build()

        notificationManager.notify(0, notification)
    }

    private fun saveActivityRecord(context: Context, newActivityRecord: ActivityRecord) {
        fun onNewLocation() {
            val thread = Thread {
                val db = ActivityDatabase.getDatabase(context)
                db.activityRecordDao().addActivityRecord(newActivityRecord)
            }
            thread.start()
        }
        MainScope().launch(Dispatchers.IO) {
            onNewLocation()
        }
    }
}
