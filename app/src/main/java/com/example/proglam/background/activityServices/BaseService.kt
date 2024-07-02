package com.example.proglam.background.activityServices


import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.proglam.ui.ongoingActivity.OngoingBaseActivity
import com.example.proglam.R
import com.example.proglam.utils.TimerEvent
import com.example.proglam.utils.ActivityService
import com.example.proglam.utils.Strings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

open class BaseService : LifecycleService(), ActivityService {

    companion object {
        val timerEvent = MutableLiveData<TimerEvent>()
        val timerInMillis = MutableLiveData<Long>()
        var errorMessage = " "
    }

    private var isServiceStopped = true
    private val NOTIFICATION_ID = 1

    override fun onCreate() {
        super.onCreate()
        initValues()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ActivityService.Actions.START.toString() -> {
                start()
            }

            ActivityService.Actions.STOP.toString() -> {
                stop()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun start() {
        Log.d("service", "service started")

        isServiceStopped = false
        timerEvent.postValue(TimerEvent.START)
        startTimer()

        startForeground(
            NOTIFICATION_ID,
            getNotificationBuilder().build(),
            FOREGROUND_SERVICE_TYPE_LOCATION
        )

        timerInMillis.observe(this, Observer {
            if (!isServiceStopped) {
                val builder = getNotificationBuilder()
                    .setContentText(Strings.formattedTimer(it/1000))
                val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(NOTIFICATION_ID, builder.build())
            }
        })
    }

    override fun stop() {
        isServiceStopped = true
        initValues()
        stopSelf()
        Log.d("service", "service stopped")
    }

    override fun abort(e: Throwable) {
        isServiceStopped = true
        timerEvent.postValue(TimerEvent.ABORT)
        timerInMillis.postValue(0L)
        Log.e("service", e.toString())
        errorMessage = e.toString()
        stopSelf()
    }

    private fun startTimer() {
        val timeStarted = System.currentTimeMillis()
        CoroutineScope(Dispatchers.Main).launch {
            while (!isServiceStopped && timerEvent.value!! == TimerEvent.START) {
                val lapTime = ((System.currentTimeMillis() - timeStarted))
                timerInMillis.postValue(lapTime)
                delay(100L)
            }
        }
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
                    Intent(this, OngoingBaseActivity::class.java).apply {
                        this.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    },
                    PendingIntent.FLAG_MUTABLE
                )
            )

    private fun initValues() {
        timerEvent.postValue(TimerEvent.END)
        timerInMillis.postValue(0L)
    }
}
