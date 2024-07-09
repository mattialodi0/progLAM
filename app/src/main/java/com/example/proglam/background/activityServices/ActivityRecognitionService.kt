package com.example.proglam.background.activityServices

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.SystemClock
import androidx.core.app.ActivityCompat
import com.example.proglam.background.ActivityRecognitionBroadcastReceiver
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
    }

    override fun onDestroy() {
        super.onDestroy()
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
                1000,
                getActivityDetectionPendingIntent()
            )
            task.addOnSuccessListener {
                sendFakeActivityTransitionEvent()
            }
        }
    }

    private fun stopActivityRecognition() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        )
        {
            val task = activityRecognitionClient.removeActivityUpdates(getActivityDetectionPendingIntent())
            task.addOnSuccessListener {
                getActivityDetectionPendingIntent().cancel()
            }
        }
    }

    private fun getActivityDetectionPendingIntent(): PendingIntent {
        val intent = Intent(this, ActivityRecognitionBroadcastReceiver::class.java)
        return PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }






    @SuppressLint("VisibleForTests")
    fun sendFakeActivityTransitionEvent() {
        val intent = Intent(this, ActivityRecognitionBroadcastReceiver::class.java)


        val events: ArrayList<ActivityTransitionEvent> = arrayListOf()
        val transitionEvent = ActivityTransitionEvent(DetectedActivity.WALKING, ActivityTransition.ACTIVITY_TRANSITION_EXIT, SystemClock.elapsedRealtimeNanos()-5000000000)
        val transitionEvent1 = ActivityTransitionEvent(DetectedActivity.RUNNING, ActivityTransition.ACTIVITY_TRANSITION_ENTER, SystemClock.elapsedRealtimeNanos())
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