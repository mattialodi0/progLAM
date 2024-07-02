package com.example.proglam.ui.startActivity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.LinearLayout.TEXT_ALIGNMENT_CENTER
import android.widget.LinearLayout.VERTICAL
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.proglam.R
import com.example.proglam.background.activityServices.BaseService
import com.example.proglam.background.activityServices.GpsPedometerService
import com.example.proglam.background.activityServices.GpsService
import com.example.proglam.background.activityServices.PedometerService
import com.example.proglam.databinding.FragmentStartActivityBinding
import com.example.proglam.db.ActivityTypeViewModel
import com.example.proglam.ui.ongoingActivity.OngoingBaseActivity
import com.example.proglam.ui.ongoingActivity.OngoingGpsActivity
import com.example.proglam.ui.ongoingActivity.OngoingGpsPedometerActivity
import com.example.proglam.ui.ongoingActivity.OngoingPedometerActivity
import com.example.proglam.utils.Permissions
import android.content.DialogInterface

import android.location.LocationManager
import android.provider.Settings
import androidx.core.location.LocationManagerCompat


class StartActivityFragment : Fragment() {
    private var _binding: FragmentStartActivityBinding? = null
    private val mStartActivityViewModel: StartActivityViewModel by viewModels()
    private var isOngoingActivityRunning = false

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartActivityBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Fill the view with data
        populateScrollViewActivityTypes(root)
        displayActivityDataBox(root)
        setStartButtonListener(root)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    // Function for the UI
    private fun populateScrollViewActivityTypes(view: View) {
        val ll: LinearLayout = view.findViewById(R.id.activityTypes_ll)

        ll.removeAllViews()

        val mActivityTypeViewModel = ViewModelProvider(this).get(ActivityTypeViewModel::class.java)
        mActivityTypeViewModel.getActivityTypes.observe(
            viewLifecycleOwner,
            Observer { activityTypes ->
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


                    hll.addView(activityTypeBtn)
                    hll.addView(activityTypeText)
                    ll.addView(hll)
                }
            })
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
    }


    val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            isOngoingActivityRunning = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun setStartButtonListener(view: View) {
        val startBtn: Button = view.findViewById(R.id.start_btn)

        startBtn.setOnClickListener {
            if (mStartActivityViewModel.selectedActivityTypeName.value != "-") {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(
                            android.Manifest.permission.BODY_SENSORS,
                            android.Manifest.permission.ACTIVITY_RECOGNITION,
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
                            val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
                            if (LocationManagerCompat.isLocationEnabled(locationManager)) {
                                intent = Intent(
                                    activity,
                                    OngoingGpsActivity::class.java
                                )
                            }
                            else {
                                Toast.makeText(requireContext(), "You need to turn on location", Toast.LENGTH_LONG ).show()
                                return@setOnClickListener
                            }
                        }
                        10 -> {
                            intent = Intent(
                                activity,
                                OngoingPedometerActivity::class.java
                            )
                        }
                        11 -> {
                            val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
                            if (LocationManagerCompat.isLocationEnabled(locationManager)) {
                                intent = Intent(
                                    activity,
                                    OngoingGpsPedometerActivity::class.java
                                )
                            }
                            else {
                                Toast.makeText(requireContext(), "You need to turn on location", Toast.LENGTH_LONG ).show()
                                return@setOnClickListener
                            }
                        }
                        else -> {
                            intent = Intent(
                                activity,
                                OngoingBaseActivity::class.java
                            )
                        }
                    }

                    intent.putExtra(
                        "activityType",
                        mStartActivityViewModel.selectedActivityTypeName.value
                    )
                    intent.putExtra("startTime", System.currentTimeMillis().toString())

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
}