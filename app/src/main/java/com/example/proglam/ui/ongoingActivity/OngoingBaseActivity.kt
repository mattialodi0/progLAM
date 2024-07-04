package com.example.proglam.ui.ongoingActivity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.proglam.R
import com.example.proglam.utils.TimerEvent
import com.example.proglam.background.activityServices.BaseService
import com.example.proglam.db.ActivityRecord
import com.example.proglam.db.ActivityRecordViewModel
import com.example.proglam.utils.ActivityService
import com.example.proglam.utils.Strings
import com.google.android.material.button.MaterialButton


class OngoingBaseActivity : AppCompatActivity() {
    //private lateinit var binding: ActivityOngoingBinding

    private var isTimerRunning = false

    private var activityType: String = ""
    private var startTime: String = ""

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //set ongoing activity data
        val extras = intent.extras
        if (extras != null) {
            if(extras.getString("activityType") != null)
                this.activityType = extras.getString("activityType")!!
            if (extras.getString("startTime") != null)
                this.startTime = extras.getString("startTime")!!
            else
                this.startTime = System.currentTimeMillis().toString()
            if(extras.getString("firstTime") != null)
                callForegroundService(ActivityService.Actions.START.toString())
        }

        setObservers()

        /* UI setup */
        //binding = ActivityOngoingBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ongoing)
        setBtnListeners()
        setupUI()
    }

    private fun setupUI() {
        val title = findViewById<TextView>(R.id.title_tv)
        title.text = activityType
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
                timer.text = Strings.formattedTimer(it/1000)
        })
    }

    private fun callForegroundService(action: String) {
        startService(
            Intent(applicationContext, BaseService::class.java).also {
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