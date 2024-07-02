package com.example.proglam.ui.ongoingActivity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.proglam.R
import com.example.proglam.utils.TimerEvent
import com.example.proglam.background.activityServices.BaseService
import com.example.proglam.background.activityServices.GpsPedometerService
import com.example.proglam.background.activityServices.GpsService
import com.example.proglam.background.activityServices.PedometerService
import com.example.proglam.databinding.ActivityOngoingBinding
import com.example.proglam.db.ActivityRecord
import com.example.proglam.db.ActivityRecordViewModel
import com.example.proglam.utils.ActivityService
import com.example.proglam.utils.Strings
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson


class OngoingPedometerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOngoingBinding

    private var isTimerRunning = false

    private var activityType: String = ""
    private var activityToolsData: String = ""
    private var startTime: String = ""

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //set ongoing activity data
        val extras = intent.extras
        if (extras != null) {
            this.activityType = extras.getString("activityType")!!
            this.startTime = extras.getString("startTime")!!
        }

        setObservers()
        callForegroundService(ActivityService.Actions.START.toString())

        /* UI setup */
        binding = ActivityOngoingBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_ongoing)
        setBtnListeners()
        setupUI()
    }

    private fun setupUI() {
        val title = findViewById<TextView>(R.id.title_tv)
        title.text = activityType
        val info = findViewById<TextView>(R.id.info_tv)
        info.text = "steps: 0"
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
                    activityToolsData = Gson().toJson((PedometerService.stepsTotal.value!! - PedometerService.stepsPrevious))
                    finish()
                }
                is TimerEvent.ABORT -> {
                    isTimerRunning = false
                }
            }
        })

        BaseService.timerInMillis.observe(this, Observer {
            val timer: TextView = findViewById(R.id.timer)
            if (it != null)
                timer.text = Strings.formattedTimer(it/1000)
        })

        PedometerService.stepsTotal.observe(this) {
            val info: TextView = findViewById(R.id.info_tv)
            info.text = "steps: ${it-PedometerService.stepsPrevious}"
        }
    }

    private fun callForegroundService(action: String) {
        startService(
            Intent(applicationContext, PedometerService::class.java).also {
                it.action = action
            }
        )
    }

    private fun registerActivityRecord() {
        val mActivityRecordViewModel = ViewModelProvider(this)[ActivityRecordViewModel::class.java]

        val activityRecord = ActivityRecord(0, activityType, startTime.toLong(), System.currentTimeMillis(), activityToolsData)

        mActivityRecordViewModel.addActivityRecord(activityRecord)
    }
}