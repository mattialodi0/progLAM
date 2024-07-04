package com.example.proglam.background.activityServices

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.proglam.background.ActivityRecognitionBroadcastReceiver
import com.example.proglam.ui.ongoingActivity.OngoingActivityRecognition
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.tasks.OnSuccessListener

class ActivityRecognitionService : GpsPedometerService() {

    private lateinit var activityRecognitionClient: ActivityRecognitionClient

    override fun onCreate() {
        super.onCreate()

        activityRecognitionClient = ActivityRecognition.getClient(this)
        startActivityRecognition()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        )
            activityRecognitionClient.removeActivityUpdates(getActivityDetectionPendingIntent())
    }

    private fun startActivityRecognition() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                ActivityCompat.requestPermissions(
                    OngoingActivityRecognition(),
                    arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                    0
                )
            else
                ActivityCompat.requestPermissions(
                    OngoingActivityRecognition(),
                    arrayOf("ACTIVITY_RECOGNITION"),
                    0
                )
        }
        val task = activityRecognitionClient.requestActivityUpdates(
            1000,
            getActivityDetectionPendingIntent()
        )

        task.addOnSuccessListener(OnSuccessListener<Void> {
            Log.d("ttt", "recognition started")

        })
    }

    private fun getActivityDetectionPendingIntent(): PendingIntent {
        val intent = Intent(this, ActivityRecognitionBroadcastReceiver::class.java)
        return PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}