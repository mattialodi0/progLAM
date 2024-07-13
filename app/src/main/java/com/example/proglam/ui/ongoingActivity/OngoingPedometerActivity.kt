package com.example.proglam.ui.ongoingActivity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.proglam.R
import com.example.proglam.utils.TimerEvent
import com.example.proglam.background.activityServices.BaseService
import com.example.proglam.background.activityServices.PedometerService
import com.example.proglam.databinding.ActivityOngoingBinding
import com.example.proglam.db.ActivityRecord
import com.example.proglam.db.ActivityRecordViewModel
import com.example.proglam.utils.ActivityService
import com.example.proglam.utils.JsonData
import com.example.proglam.utils.Strings
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson


class OngoingPedometerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOngoingBinding

    private var isTimerRunning = false

    private var activityType: String = ""
    private var activityToolsData: String = ""
    private var startTime: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //set ongoing activity data
        val extras = intent.extras
        if (extras != null && savedInstanceState == null) {
            if(extras.getString("activityType") != null)
                this.activityType = extras.getString("activityType")!!
            if (extras.getString("startTime") != null)
                this.startTime = extras.getString("startTime")!!
            else
                this.startTime = System.currentTimeMillis().toString()
            if(extras.getString("firstTime") != null){
                callForegroundService(ActivityService.Actions.START.toString(), activityType)
            }
        }

        setObservers()

        /* UI setup */
        binding = ActivityOngoingBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_ongoing)
        setBtnListeners()
        setupUI()

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                callForegroundService(ActivityService.Actions.STOP.toString())
                finish()
            }
        })
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
            try {
                val steps = if(PedometerService.stepsTotal.value != null) PedometerService.stepsTotal.value!! - PedometerService.stepsPrevious else 0
                activityToolsData = Gson().toJson(JsonData(ArrayList<LatLng>(), steps))
            } catch (e: NullPointerException) {
                activityToolsData = "{}"
            }

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
        BaseService.timerEvent.observe(this) {
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
                }
            }
        }

        BaseService.timerInMillis.observe(this, Observer {
            val timer: TextView = findViewById(R.id.timer)
            if (it != null)
                timer.text = Strings.formattedTimer(it/1000)
        })

        PedometerService.stepsTotal.observe(this) {
            val info: TextView = findViewById(R.id.info_tv)
            info.text = String.format(resources.getString(R.string.steps_num), (it-PedometerService.stepsPrevious))
        }
    }

    private fun callForegroundService(action: String, activityType:String="") {
        startService(
            Intent(applicationContext, PedometerService::class.java).also {
                it.action = action
                if(activityType != "")
                    it.putExtra("activityType", activityType)
            }
        )
    }

    private fun registerActivityRecord() {
        val mActivityRecordViewModel = ViewModelProvider(this)[ActivityRecordViewModel::class.java]

        val a = if(activityType == null) "None" else activityType
        val s = if(startTime.isEmpty()) System.currentTimeMillis()-1000 else startTime.toLong()
        val t = activityToolsData.ifEmpty { "{}" }
        try {
            val activityRecord = ActivityRecord(0, a, s, System.currentTimeMillis(), t)
            mActivityRecordViewModel.addActivityRecord(activityRecord)
        } catch (e: NullPointerException) {
            Log.i("OngoingPedometerActivity", "NullPr exception saving record to the DB")
        }
    }
}