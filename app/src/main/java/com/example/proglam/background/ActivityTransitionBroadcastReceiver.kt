package com.example.proglam.background

import android.app.Notification
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
import com.example.proglam.utils.Notifications
import com.google.android.gms.location.ActivityTransitionResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ActivityTransitionBroadcastReceiver : BroadcastReceiver() {
    companion object {
        @JvmStatic
        private var savedStartTime = 0L
    }

    override fun onReceive(context: Context, intent: Intent) {

        if (ActivityTransitionResult.hasResult(intent)) {
            val result = ActivityTransitionResult.extractResult(intent)
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
                        //sendNotification(context, activityType, 0)
                        val diff = SystemClock.elapsedRealtimeNanos() / 1000000 - System.currentTimeMillis()
                        savedStartTime = event.elapsedRealTimeNanos / 1000000 - diff
                    }
                    else if (transitionType == "EXIT") {
                        val startTime = savedStartTime
                        savedStartTime = 0L
                        val diff = SystemClock.elapsedRealtimeNanos() / 1000000 - System.currentTimeMillis()
                        val finishTime = event.elapsedRealTimeNanos / 1000000 - diff

                        if (finishTime - startTime >= 60000) {
                            val newActivityRecord = ActivityRecord(
                                0,
                                activityType,
                                startTime,
                                finishTime,
                                "{}"
                            )
                            saveActivityRecord(context, newActivityRecord)
                            sendNotification(context, activityType, 1)
                        } else
                            sendNotification(context, activityType, 2)
                    }
                }
            }
        }
    }

    private fun sendNotification(context: Context, activityType: String, transition: Int) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = Notifications.CHANNEL_ID
        val title = when (transition) {
            0 -> "Activity started"
            1 -> "Activity recorded"
            else -> "Activity to short to be register"
        }

        val notification = Notification.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText("type: $activityType")
            .setSmallIcon(R.drawable.ic_activitytype_generic)
            .build()
        notificationManager.notify(0, notification)

    }

    private fun saveActivityRecord(context: Context, newActivityRecord: ActivityRecord) {
        fun saveToDb() {
            val thread = Thread {
                val db = ActivityDatabase.getDatabase(context)
                db.activityRecordDao().addActivityRecord(newActivityRecord)
            }
            thread.start()
        }
        MainScope().launch(Dispatchers.IO) {
            saveToDb()
        }
    }
}
