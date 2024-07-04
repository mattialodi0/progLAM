package com.example.proglam.ui.ongoingActivity

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.proglam.R
import com.example.proglam.background.activityServices.ActivityRecognitionService
import com.example.proglam.background.activityServices.BaseService
import com.example.proglam.background.activityServices.GpsPedometerService
import com.example.proglam.background.activityServices.GpsService
import com.example.proglam.db.ActivityRecord
import com.example.proglam.db.ActivityRecordViewModel
import com.example.proglam.utils.ActivityService
import com.example.proglam.utils.Strings
import com.example.proglam.utils.TimerEvent
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson


class OngoingActivityRecognition : AppCompatActivity() {
    private var isTimerRunning = false

    private var activityType: String = ""
    private var activityToolsData: String = "{}"
    private var startTime: String = ""

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //set ongoing activity data
        val extras = intent.extras
        if (extras != null) {
            if (extras.getString("startTime") != null)
                this.startTime = extras.getString("startTime")!!
            else
                this.startTime = System.currentTimeMillis().toString()
            if(extras.getString("firstTime") != null) {
                //callForegroundService(ActivityService.Actions.START.toString())
                val intent = Intent(this, ActivityRecognitionService::class.java).also {
                    it.action = ActivityService.Actions.START.toString()
                }
                val pendingIntent  =
                    PendingIntent.getService(
                        this,
                        101,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                try {
                    pendingIntent.send()
                } catch (e: PendingIntent.CanceledException) {
                    e.printStackTrace()
                }
            }
        }

        setObservers()
        //setReceiver()

        /* UI setup */
        enableEdgeToEdge()
        setContentView(R.layout.activity_ongoing)
        setBtnListeners()
        setupUI()
    }

    private fun setupUI() {
        val title = findViewById<TextView>(R.id.title_tv)
        title.text = "Auto activity recognition"
    }

    private fun setBtnListeners() {

        val stopBtn = findViewById<MaterialButton>(R.id.stop_btn)
        stopBtn.setOnClickListener {
            callForegroundService(ActivityService.Actions.STOP.toString())
            registerActivityRecord()
            finish()
        }

        val deleteBtn = findViewById<MaterialButton>(R.id.delete_btn)
        deleteBtn.setOnClickListener {
            callForegroundService(ActivityService.Actions.STOP.toString())
            finish()
        }
    }

    private fun setObservers() {
        BaseService.timerEvent.observe(this, Observer {
            when (it) {
                is TimerEvent.START -> {
                    isTimerRunning = true
                }

                is TimerEvent.END -> {
                    isTimerRunning = false
                    activityToolsData = Gson().toJson(GpsService.locations.value) + " " +
                            Gson().toJson((GpsPedometerService.stepsTotal.value!! - GpsPedometerService.stepsPrevious))
                    finish()
                }

                is TimerEvent.ABORT -> {
                    isTimerRunning = false
                    finish()
                }
            }
        })

        BaseService.timerInMillis.observe(this, Observer {
            val timer: TextView = findViewById(R.id.timer)
            if (it != null)
                timer.text = Strings.formattedTimer(it / 1000)
        })
    }
/*

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun setReceiver() {
        val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                val extras = intent.extras
                val activityType = extras?.getString("activityType")
                val transitionType = extras?.getString("transitionType")
                if (activityType != null) {
                    Log.e("rrr", activityType)
                }
                if (transitionType != null) {
                    Log.e("rrr", transitionType)
                }
            }
        }
        registerReceiver(broadcastReceiver, IntentFilter("ACTIVITY_TRANSITION"),
            RECEIVER_NOT_EXPORTED
        )
    }

 */

    private fun callForegroundService(action: String) {
        startService(
            Intent(applicationContext, ActivityRecognitionService::class.java).also {
                it.action = action
            }
        )
    }

    private fun registerActivityRecord() {
        val mActivityRecordViewModel = ViewModelProvider(this)[ActivityRecordViewModel::class.java]

        val activityRecord =
            ActivityRecord(0, activityType, startTime.toLong(), System.currentTimeMillis(), "{}")

        mActivityRecordViewModel.addActivityRecord(activityRecord)
    }
}