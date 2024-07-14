package com.example.proglam.background

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
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
                    .setSmallIcon(R.drawable.ic_splashscreen)
                    .setContentTitle("Activity tracker")
                    .setContentText("It is time to register an activity")
                    .build()

                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(1, notification)
            } else if (intent.action == "REGISTER_NONE_ACTIVITY")
                registerNoneActivity(context)
        }
    }

    private fun registerNoneActivity(context: Context) {
        fun callback() {
            val thread = Thread {
                val db = ActivityDatabase.getDatabase(context)
                val startTime = System.currentTimeMillis() - ((24 * 3600 * 1000) - (10 * 60 * 1000))
                val todayActivities = db.activityRecordDao().getTodayActivitiesList(startTime)
                val ars: List<ActivityRecord> = todayActivities

                var sum = 0L
                for (a in ars) {
                    sum += a.finishTime - a.startTime
                }
                var noneTime = ((24 * 3600 * 1000) - (10 * 60 * 1000)) - sum
                if(noneTime < 0) noneTime = 0
                else if(noneTime > 86400000L) noneTime = 86400000L

                Log.i("AlarmReceiver", "Registered a none activity of ${noneTime} ms")
                db.activityRecordDao().addActivityRecord(
                    ActivityRecord(
                        0,
                        "none",
                        System.currentTimeMillis() - noneTime,
                        System.currentTimeMillis(),
                        "{}"
                    )
                )
            }
            thread.start()
        }
        MainScope().launch(Dispatchers.IO) {callback()}
    }
}