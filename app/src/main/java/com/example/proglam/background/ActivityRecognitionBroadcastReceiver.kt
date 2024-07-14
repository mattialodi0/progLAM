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
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ActivityRecognitionBroadcastReceiver : BroadcastReceiver() {
    companion object {
        @JvmStatic
        private var savedStartTime = 0L
        @JvmStatic
        private var activityType = ""
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "START_RECOGNITION") {
            savedStartTime = 0L
            activityType = ""
        } else if (intent.action == "STOP_RECOGNITION") {
            if (activityType != "") {
                val startTime = savedStartTime
                val finishTime = System.currentTimeMillis()

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

                savedStartTime = 0L
                activityType = ""
            }
        } else if (ActivityRecognitionResult.hasResult(intent)) {
            val result = ActivityRecognitionResult.extractResult(intent)
            if (result != null) {
                val type = when (result.mostProbableActivity.type) {
                    DetectedActivity.IN_VEHICLE -> "in vehicle"
                    DetectedActivity.ON_BICYCLE -> "in vehicle"
                    DetectedActivity.RUNNING -> "run"
                    DetectedActivity.WALKING -> "walk"
                    DetectedActivity.ON_FOOT -> "rest"
                    DetectedActivity.STILL -> "rest"
                    else -> "none"
                }
                val confidence = result.mostProbableActivity.confidence

                if (confidence >= 50 && type != "none") {
                    val info = "Detection: " + type + " (" + confidence + ")" + "   " +
                            SimpleDateFormat("HH:mm:ss", Locale.ITALY).format(Date())
                    Log.i("ActivityRecognitionBroadcastReceiver", info)

                    if (activityType == "") {
                        activityType = type
                        savedStartTime = System.currentTimeMillis()
                    } else if (type != activityType) {
                        val startTime = savedStartTime
                        val finishTime = System.currentTimeMillis()

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
                        }

                        activityType = type
                        savedStartTime = System.currentTimeMillis()
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
            .setSmallIcon(R.drawable.ic_splashscreen)
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