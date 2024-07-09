package com.example.proglam.ui.startActivity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.LinearLayout.TEXT_ALIGNMENT_CENTER
import android.widget.LinearLayout.VERTICAL
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.proglam.R
import com.example.proglam.background.activityServices.ActivityRecognitionService
import com.example.proglam.databinding.FragmentStartActivityBinding
import com.example.proglam.db.ActivityTypeViewModel
import com.example.proglam.ui.ongoingActivity.OngoingBaseActivity
import com.example.proglam.ui.ongoingActivity.OngoingGpsActivity
import com.example.proglam.ui.ongoingActivity.OngoingGpsPedometerActivity
import com.example.proglam.ui.ongoingActivity.OngoingPedometerActivity
import com.google.android.material.switchmaterial.SwitchMaterial


class StartActivityFragment : Fragment() {
    private var _binding: FragmentStartActivityBinding? = null
    private val mStartActivityViewModel: StartActivityViewModel by viewModels()
    private var isOngoingActivityRunning = false
    private lateinit var locationManager: LocationManager

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartActivityBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setObserver(root)
        // Fill the view with data
        populateScrollViewActivityTypes(root)
        displayActivityDataBox(root)
        setButtonListeners(root)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setObserver(view: View) {
        mStartActivityViewModel.autoRec.observe(viewLifecycleOwner) {
            val sv = view.findViewById<ScrollView>(R.id.activityTypes_sv)
            if (it)
                sv.visibility = ScrollView.INVISIBLE
            else
                sv.visibility = ScrollView.VISIBLE

            view.findViewById<TextView>(R.id.activityDescName_tv).text = ""
            view.findViewById<TextView>(R.id.activityDescTools_tv).text = ""
        }
    }


    // Function for the UI
    private fun populateScrollViewActivityTypes(view: View) {
        val ll: LinearLayout = view.findViewById(R.id.activityTypes_ll)
        ll.removeAllViews()

        val mActivityTypeViewModel = ViewModelProvider(this)[ActivityTypeViewModel::class.java]
        mActivityTypeViewModel.getActivityTypes.observe(viewLifecycleOwner) { at ->
            val activityTypes = at.sortedBy { it.name }
            var i = 0
            for (activityType in activityTypes) {
                if (i > activityTypes.size)
                    break
                i++

                val hll = LinearLayout(activity)
                val activityTypeBtn = Button(activity)
                val activityTypeText = TextView(activity)

                activityTypeText.text = activityType.name.toString()
                activityTypeText.textAlignment = TEXT_ALIGNMENT_CENTER
                hll.orientation = VERTICAL
                if (com.example.proglam.utils.System.isNightModeOn(requireContext())) {
                    val color = ContextCompat.getColor(requireContext(), R.color.gray_700)
                    hll.setBackgroundColor(color)
                } else {
                    val color = ContextCompat.getColor(requireContext(), R.color.gray_200)
                    hll.setBackgroundColor(color)
                }

                val context: Context = ll.context
                //activityTypeBtn.background = ContextCompat.getDrawable(requireContext(), R.drawable.round_button)
                activityTypeBtn.background = ContextCompat.getDrawable(
                    requireContext(),
                    context.resources.getIdentifier(
                        activityType.iconSrc.toString(),
                        "drawable",
                        context.packageName
                    )
                )

                val dpInPx = com.example.proglam.utils.System.floatToDP(64F, resources)
                activityTypeBtn.setLayoutParams(
                    LinearLayout.LayoutParams(
                        dpInPx.toInt(),
                        dpInPx.toInt()
                    )
                )

                activityTypeBtn.setOnClickListener {
                    mStartActivityViewModel.setSelectedActivityTypeName(activityType.name)
                    mStartActivityViewModel.setSelectedActivityType(activityType)
                }
                activityTypeBtn.setOnLongClickListener {
                    AlertDialog.Builder(context)
                        .setTitle("Delete activity type")
                        .setMessage("Are you sure you want to delete \"${activityType.name}\"?")
                        .setPositiveButton(android.R.string.yes
                        ) { _, _ ->
                            mActivityTypeViewModel.removeActivityType(activityType)
                            Toast.makeText(
                                requireContext(),
                                "deleted ${activityType.name}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show()
                    return@setOnLongClickListener true
                }

                hll.addView(activityTypeBtn)
                hll.addView(activityTypeText)
                ll.addView(hll)
            }
        }
    }

    private fun displayActivityDataBox(view: View) {
        mStartActivityViewModel.selectedActivityType.observe(
            viewLifecycleOwner
        ) { currentActivityType ->
            val acNameTV = view.findViewById<TextView>(R.id.activityDescName_tv)
            acNameTV.text = currentActivityType.name
            acNameTV.textAlignment = TEXT_ALIGNMENT_CENTER
            acNameTV.textSize = com.example.proglam.utils.System.floatToSP(24F, resources)

            var tools = ""
            when (currentActivityType.tools) {
                0 -> tools = "no tools used"
                1 -> tools = "GPS"
                10 -> tools = "pedometer"
                11 -> tools = "GPS + pedometer"
            }
            val acToolsTV = view.findViewById<TextView>(R.id.activityDescTools_tv)
            acToolsTV.text = tools
        }

        mStartActivityViewModel.autoRec.observe(viewLifecycleOwner) {
            if (it) {

                activity?.startService(Intent(activity, ActivityRecognitionService::class.java))
            } else {
                activity?.stopService(Intent(activity, ActivityRecognitionService::class.java))
            }
        }
    }


    val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            isOngoingActivityRunning = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun setButtonListeners(view: View) {
        val startBtn: Button = view.findViewById(R.id.start_btn)
        startBtn.setOnClickListener {
            startButtonFunction()
        }

        val switch = view.findViewById<SwitchMaterial>(R.id.autoActivityRecognition_switch)
        switch.setOnCheckedChangeListener { _, isChecked ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        android.Manifest.permission.BODY_SENSORS,
                        android.Manifest.permission.ACTIVITY_RECOGNITION,
                        android.Manifest.permission.INTERNET,
                        android.Manifest.permission.FOREGROUND_SERVICE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    0
                )
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        "BODY_SENSORS",
                        "ACTIVITY_RECOGNITION",
                        "INTERNET",
                        "FOREGROUND_SERVICE_LOCATION",
                        "ACCESS_COARSE_LOCATION",
                        "ACCESS_FINE_LOCATION"
                    ),
                    0
                )
            }
            if (LocationManagerCompat.isLocationEnabled(locationManager)) {
                mStartActivityViewModel.autoRec.postValue(isChecked)
            } else {
                switch.isChecked = false
                Toast.makeText(requireContext(), "You need to turn on location", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun startButtonFunction() {
        if (mStartActivityViewModel.autoRec.value == true) {
            Toast.makeText(
                requireContext(),
                "Already recording activity automatically",
                Toast.LENGTH_LONG
            ).show()
        } else if (mStartActivityViewModel.selectedActivityTypeName.value != "-") {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        android.Manifest.permission.BODY_SENSORS,
                        android.Manifest.permission.ACTIVITY_RECOGNITION,
                        android.Manifest.permission.INTERNET,
                        android.Manifest.permission.FOREGROUND_SERVICE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    0
                )
            else
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        "BODY_SENSORS",
                        "ACTIVITY_RECOGNITION",
                        "INTERNET",
                        "FOREGROUND_SERVICE_LOCATION",
                        "ACCESS_COARSE_LOCATION",
                        "ACCESS_FINE_LOCATION"
                    ),
                    0
                )

            //if (!isOngoingActivityRunning) {
            //isOngoingActivityRunning = true
            var intent = Intent()

            when (mStartActivityViewModel.selectedActivityType.value?.tools) {
                1 -> {
                    if (LocationManagerCompat.isLocationEnabled(locationManager)) {
                        intent = Intent(
                            activity,
                            OngoingGpsActivity::class.java
                        )
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "You need to turn on location",
                            Toast.LENGTH_LONG
                        ).show()
                        return
                    }
                }

                10 -> {
                    intent = Intent(
                        activity,
                        OngoingPedometerActivity::class.java
                    )
                }

                11 -> {
                    if (LocationManagerCompat.isLocationEnabled(locationManager)) {
                        intent = Intent(
                            activity,
                            OngoingGpsPedometerActivity::class.java
                        )
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "You need to turn on location",
                            Toast.LENGTH_LONG
                        ).show()
                        return
                    }
                }

                else -> {
                    intent = Intent(
                        activity,
                        OngoingBaseActivity::class.java
                    )
                }
            }

            intent.putExtra("activityType", mStartActivityViewModel.selectedActivityTypeName.value)
            intent.putExtra("startTime", System.currentTimeMillis().toString())
            intent.putExtra("firstTime", true.toString())

            startForResult.launch(intent)
            /*
            } else
                Toast.makeText(
                    requireContext(),
                    "An activity is already on",
                    Toast.LENGTH_SHORT
                ).show()
             */
        } else
            Toast.makeText(
                requireContext(),
                "You need to choose an activity type",
                Toast.LENGTH_SHORT
            ).show()
    }
}