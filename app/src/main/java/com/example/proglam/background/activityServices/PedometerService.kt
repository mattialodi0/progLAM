package com.example.proglam.background.activityServices

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.example.proglam.R
import com.example.proglam.ui.ongoingActivity.OngoingGpsActivity
import com.example.proglam.ui.ongoingActivity.OngoingPedometerActivity

open class PedometerService: BaseService(), SensorEventListener {

    companion object {
        val stepsTotal = MutableLiveData<Int>()
        var stepsPrevious = 0
    }


    private var sensorManager: SensorManager? = null
    private var setted = false

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
            if(stepSensor == null) {
            Toast.makeText(this, "No step sensor on device", Toast.LENGTH_SHORT).show()
        }
        else {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        stepsTotal.postValue(event!!.values[0].toInt())
        if(!setted) {
            stepsPrevious = event.values[0].toInt()
            setted = true
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun getNotificationBuilder(): NotificationCompat.Builder =
        NotificationCompat.Builder(this, "ongoing-channel")
            .setAutoCancel(false)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSmallIcon(R.drawable.ic_activitytype_generic)
            .setContentTitle("Run is active")
            .setContentText("00:00:00")
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    143,
                    Intent(this, OngoingPedometerActivity::class.java).apply {
                        this.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    },
                    PendingIntent.FLAG_MUTABLE
                )
            )
}