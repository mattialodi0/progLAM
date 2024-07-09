package com.example.proglam.background

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import com.example.proglam.R
import com.example.proglam.db.ActivityDatabase
import com.example.proglam.db.ActivityRecord
import com.example.proglam.db.ActivityRecordViewModel
import com.example.proglam.utils.ActivityTransitionsUtil
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
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
                    val transitionType = ActivityTransitionsUtil.toTransitionType(event.transitionType)
                    val info =
                        "Transition: " + activityType +
                                " (" + transitionType + ")" + "   " +
                                SimpleDateFormat("HH:mm:ss", Locale.ITALY).format(Date())

                    Log.i("ActivityRecognitionBroadcastReceiver", info)

                    if(transitionType == "ENTER") {

                    }
                    else if(transitionType == "EXIT") {
                        val newActivityRecord = ActivityRecord(
                            0,
                            activityType,
                            System.currentTimeMillis()-((SystemClock.elapsedRealtimeNanos()-event.elapsedRealTimeNanos)/1000000),
                            System.currentTimeMillis(),
                            "{}"
                        )
                        saveActivityRecord(context, newActivityRecord)
                        sendNotification(context, activityType)
                    }
                }
            }
        }
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
            .setContentTitle("Activity recorded")
            .setContentText("type: $activityType")
            .setSmallIcon(R.drawable.ic_activitytype_generic)
            .build()

        notificationManager.notify(0, notification)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun saveActivityRecord(context: Context, newActivityRecord: ActivityRecord) {
        suspend fun onNewLocation() {
            val thread = Thread {
                val db = ActivityDatabase.getDatabase(context)
                db.activityRecordDao().addActivityRecord(newActivityRecord)
            }
            thread.start()
        }
        GlobalScope.launch (Dispatchers.Main) {
            onNewLocation()
        }
    }
}
