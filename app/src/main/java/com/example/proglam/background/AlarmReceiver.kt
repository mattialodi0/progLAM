package com.example.proglam.background

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.proglam.R
import com.example.proglam.db.ActivityDatabase
import com.example.proglam.db.ActivityRecord
import com.example.proglam.utils.Notifications
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent != null) {
            if (intent.action == "REMIND_TRACKING") {
                val notification = NotificationCompat.Builder(context, Notifications.CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_activitytype_generic)
                    .setContentTitle("Activity tracker")
                    .setContentText("It is time to register an activity")
                    .build()

                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(1, notification)
            } else if (intent.action == "REGISTER_NONE_ACTIVITY")
                registerNoneActivity(context)
        }

        Log.e("AlarmReceiver", "Null intent")
    }

    private fun registerNoneActivity(context: Context) {
        fun callback() {
            val thread = Thread {
                val db = ActivityDatabase.getDatabase(context)
                val startTime = System.currentTimeMillis() - (12 * 3600 * 1000 - 10 * 60 * 1000)
                val todayActivities = db.activityRecordDao().getTodayActivitiesList(startTime)
                val ars: List<ActivityRecord> = todayActivities
                Log.d("nnn", ars.toString())

                var sum = 0L
                for (a in ars) {
                    sum += a.finishTime - a.startTime
                }
                val noneTime = 24 * 3600 * 1000 - sum
                Log.d("nnn", noneTime.toString())

                db.activityRecordDao().addActivityRecord(
                    ActivityRecord(
                        0,
                        "none",
                        System.currentTimeMillis() - noneTime, System.currentTimeMillis(),
                        "{}"
                    )
                )
            }
            thread.start()
        }
        MainScope().launch(Dispatchers.IO) {callback()}
    }
}