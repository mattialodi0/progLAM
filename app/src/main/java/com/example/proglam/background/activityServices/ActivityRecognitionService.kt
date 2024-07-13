package com.example.proglam.background.activityServices

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.proglam.R
import com.example.proglam.background.ActivityRecognitionBroadcastReceiver
import com.example.proglam.background.ActivityTransitionBroadcastReceiver
import com.example.proglam.utils.ActivityTransitionsUtil
import com.example.proglam.utils.Notifications
import com.google.android.gms.common.internal.safeparcel.SafeParcelableSerializer
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionEvent
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity


class ActivityRecognitionService : Service() {
    private lateinit var activityRecognitionClient: ActivityRecognitionClient

    override fun onCreate() {
        super.onCreate()

        activityRecognitionClient = ActivityRecognition.getClient(this)
        startActivityRecognition()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            startForeground(
                109,
                getNotificationBuilder().build(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            )
        else
            startForeground(
                109,
                getNotificationBuilder().build()
            )
    }

    override fun onDestroy() {
        super.onDestroy()
        sendBroadcast(Intent(this, ActivityRecognitionBroadcastReceiver::class.java).also {
            it.action = "STOP_RECOGNITION"
        })
        stopActivityRecognition()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun startActivityRecognition() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val task = activityRecognitionClient.requestActivityUpdates(
                1000L,
                getActivityDetectionPendingIntent()
            )
            task.addOnSuccessListener {
                sendBroadcast(Intent(this, ActivityRecognitionBroadcastReceiver::class.java).also {
                    it.action = "START_RECOGNITION"
                })
            }
        }
    }

    private fun startActivityTransitionRecognition() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val task = activityRecognitionClient.requestActivityTransitionUpdates(
                ActivityTransitionsUtil.getActivityTransitionRequest(),
                getActivityTransitionPendingIntent()
            )
            task.addOnSuccessListener {
                //sendTestActivityTransitionEvent()
            }
        }
    }

    private fun stopActivityRecognition() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val task =
                activityRecognitionClient.removeActivityUpdates(getActivityDetectionPendingIntent())
            task.addOnSuccessListener {
                getActivityDetectionPendingIntent().cancel()
            }
            /*
            val task =
                activityRecognitionClient.removeActivityUpdates(getActivityDetectionPendingIntent())
            task.addOnSuccessListener {
                getActivityDetectionPendingIntent().cancel()
            }
             */
        }
    }

    private fun getNotificationBuilder(): NotificationCompat.Builder =
        NotificationCompat.Builder(this, Notifications.ONGOING_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSmallIcon(R.drawable.ic_activitytype_generic)
            .setContentTitle("Detecting activities automatically")


    private fun getActivityDetectionPendingIntent(): PendingIntent {
        val intent = Intent(this, ActivityRecognitionBroadcastReceiver::class.java)
        return PendingIntent.getBroadcast(
            this,
            122,
            intent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun getActivityTransitionPendingIntent(): PendingIntent {
        val intent = Intent(this, ActivityTransitionBroadcastReceiver::class.java)
        return PendingIntent.getBroadcast(
            this,
            122,
            intent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    fun sendTestActivityTransitionEvent() {
        val intent = Intent(this, ActivityRecognitionBroadcastReceiver::class.java)


        val events: ArrayList<ActivityTransitionEvent> = arrayListOf()
        val transitionEvent = ActivityTransitionEvent(
            DetectedActivity.WALKING,
            ActivityTransition.ACTIVITY_TRANSITION_ENTER,
            SystemClock.elapsedRealtimeNanos() - 60000000000
        )
        val transitionEvent1 = ActivityTransitionEvent(
            DetectedActivity.WALKING,
            ActivityTransition.ACTIVITY_TRANSITION_EXIT,
            SystemClock.elapsedRealtimeNanos()
        )
        events.add(transitionEvent)
        events.add(transitionEvent1)
        val result = ActivityTransitionResult(events)

        SafeParcelableSerializer.serializeToIntentExtra(
            result,
            intent,
            "com.google.android.location.internal.EXTRA_ACTIVITY_TRANSITION_RESULT"
        )

        this.sendBroadcast(intent)
    }
}