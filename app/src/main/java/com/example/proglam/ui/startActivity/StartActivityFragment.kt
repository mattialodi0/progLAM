package com.example.proglam.ui.startActivity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proglam.R
import com.example.proglam.background.activityServices.ActivityRecognitionService
import com.example.proglam.databinding.FragmentStartActivityBinding
import com.example.proglam.db.ActivityTypeViewModel
import com.example.proglam.list.ATRecyclerviewAdapter
import com.example.proglam.list.ATRecyclerviewInterface
import com.example.proglam.ui.ongoingActivity.OngoingBaseActivity
import com.example.proglam.ui.ongoingActivity.OngoingGpsActivity
import com.example.proglam.ui.ongoingActivity.OngoingGpsPedometerActivity
import com.example.proglam.ui.ongoingActivity.OngoingPedometerActivity
import com.google.android.material.switchmaterial.SwitchMaterial


class StartActivityFragment : Fragment(), ATRecyclerviewInterface, View.OnClickListener {
    private var _binding: FragmentStartActivityBinding? = null
    private val mStartActivityViewModel: StartActivityViewModel by viewModels()
    private val mActivityTypeViewModel: ActivityTypeViewModel by viewModels()
    private lateinit var locationManager: LocationManager

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartActivityBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val recyclerView = root.findViewById<RecyclerView>(R.id.activityTypes_rv)
        val adapter = ATRecyclerviewAdapter(
            requireContext(),
            this,
            mActivityTypeViewModel.getActivityTypes,
            viewLifecycleOwner
        )
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter

        setObservers(root)
        displayActivityDataBox(root)
        setButtonListeners(root)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setObservers(view: View) {
        mStartActivityViewModel.autoRec.observe(viewLifecycleOwner) {
            val rv = view.findViewById<RecyclerView>(R.id.activityTypes_rv)
            if (it)
                rv.visibility = RecyclerView.INVISIBLE
            else
                rv.visibility = RecyclerView.VISIBLE

            view.findViewById<TextView>(R.id.infoName_tv).text = ""
            val acDescTV = view.findViewById<TextView>(R.id.infoDesc_tv)
            acDescTV.text = ""
            acDescTV.visibility = TextView.GONE
            val acToolsTV = view.findViewById<TextView>(R.id.infoTools_tv)
            acToolsTV.text = ""
            acToolsTV.visibility = TextView.GONE
        }
    }

    private fun displayActivityDataBox(view: View) {
        mStartActivityViewModel.selectedActivityType.observe(
            viewLifecycleOwner
        ) { currentActivityType ->
            val acNameTV = view.findViewById<TextView>(R.id.infoName_tv)
            acNameTV.text = currentActivityType.name

            val acDescTV = view.findViewById<TextView>(R.id.infoDesc_tv)
            if (currentActivityType.desc.isNotEmpty()) {
                acDescTV.text = currentActivityType.desc
                acDescTV.visibility = TextView.VISIBLE
            } else {
                acDescTV.text = currentActivityType.desc
                acDescTV.visibility = TextView.GONE
            }

            var tools = ""
            when (currentActivityType.tools) {
                0 -> tools = "no tools used"
                1 -> tools = "GPS"
                10 -> tools = "pedometer"
                11 -> tools = "GPS + pedometer"
            }
            val acToolsTV = view.findViewById<TextView>(R.id.infoTools_tv)
            if (tools.isNotEmpty()) {
                acToolsTV.text = tools
                acToolsTV.visibility = TextView.VISIBLE
            }
        }

        mStartActivityViewModel.autoRec.observe(viewLifecycleOwner) {
            if (it) {
                activity?.startService(Intent(activity, ActivityRecognitionService::class.java))
            } else {
                activity?.stopService(Intent(activity, ActivityRecognitionService::class.java))
            }
        }
    }


    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK || result.resultCode == Activity.RESULT_CANCELED) {
            mStartActivityViewModel.isOngoingActivityRunning = false
        }
    }

    private fun setButtonListeners(view: View) {
        val startBtn: Button = view.findViewById(R.id.start_btn)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            startBtn.setOnClickListener {
                startButtonFunction()
            }
        else
            startBtn.setOnClickListener(this)

        val switch = view.findViewById<SwitchMaterial>(R.id.autoActivityRecognition_switch)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            switch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (
                        ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACTIVITY_RECOGNITION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        mStartActivityViewModel.autoRec.postValue(true)
                    } else {
                        switch.isChecked = false
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                            ActivityCompat.requestPermissions(
                                requireActivity(),
                                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                                0
                            )
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            ActivityCompat.requestPermissions(
                                requireActivity(),
                                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                                0
                            )
                        } else {
                            ActivityCompat.requestPermissions(
                                requireActivity(),
                                arrayOf("ACTIVITY_RECOGNITION"),
                                0
                            )
                        }
                    }
                } else
                    mStartActivityViewModel.autoRec.postValue(false)
            }
        } else {
            switch.visibility = View.INVISIBLE
        }
    }

    private fun startButtonFunction() {
        if (mStartActivityViewModel.autoRec.value == true) {
            Toast.makeText(
                requireContext(),
                "Already recording activity automatically",
                Toast.LENGTH_LONG
            ).show()
        } else {
            if (!mStartActivityViewModel.isOngoingActivityRunning) {
                if (mStartActivityViewModel.selectedActivityTypeName.value != "-") {
                    if (
                        ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.BODY_SENSORS
                        ) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACTIVITY_RECOGNITION
                        ) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.FOREGROUND_SERVICE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        val intent: Intent
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

                        intent.putExtra(
                            "activityType",
                            mStartActivityViewModel.selectedActivityTypeName.value
                        )
                        intent.putExtra("startTime", System.currentTimeMillis().toString())
                        intent.putExtra("firstTime", true.toString())

                        mStartActivityViewModel.isOngoingActivityRunning = true
                        startForResult.launch(intent)
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
                            ActivityCompat.requestPermissions(
                                requireActivity(),
                                arrayOf(
                                    Manifest.permission.BODY_SENSORS,
                                    Manifest.permission.ACTIVITY_RECOGNITION,
                                    Manifest.permission.FOREGROUND_SERVICE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ),
                                0
                            )
                        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                            ActivityCompat.requestPermissions(
                                requireActivity(),
                                arrayOf(
                                    Manifest.permission.BODY_SENSORS,
                                    Manifest.permission.ACTIVITY_RECOGNITION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ),
                                0
                            )
                        else
                            ActivityCompat.requestPermissions(
                                requireActivity(),
                                arrayOf(
                                    "BODY_SENSORS",
                                    "ACTIVITY_RECOGNITION",
                                    "ACCESS_COARSE_LOCATION",
                                    "ACCESS_FINE_LOCATION"
                                ),
                                0
                            )
                    }
                } else
                    Toast.makeText(
                        requireContext(),
                        "You need to choose an activity type",
                        Toast.LENGTH_SHORT
                    ).show()

            } else
                Toast.makeText(
                    requireContext(),
                    "An activity is already on",
                    Toast.LENGTH_SHORT
                ).show()
        }
    }

    override fun onItemClick(pos: Int) {
        val ats = mActivityTypeViewModel.getActivityTypes.value
        if (ats != null) {
            mStartActivityViewModel.setSelectedActivityTypeName(ats[pos].name)
            mStartActivityViewModel.setSelectedActivityType(ats[pos])
        }
    }

    override fun onItemLongClick(pos: Int) {
        val ats = mActivityTypeViewModel.getActivityTypes.value
        if (ats != null) {
            if (ats[pos].name !in arrayOf("run", "sleep", "walk", "rest", "in vehicle"))
                AlertDialog.Builder(context)
                    .setTitle("Delete activity type")
                    .setMessage("Are you sure you want to delete \"${ats[pos].name}\"?")
                    .setPositiveButton("yes") { _, _ ->
                        mActivityTypeViewModel.removeActivityType(ats[pos])
                        Toast.makeText(
                            requireContext(),
                            "deleted ${ats[pos].name}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .setNegativeButton("no", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
        }

        return
    }

    override fun onClick(v: View?) {
        if (mStartActivityViewModel.autoRec.value == true) {
            Toast.makeText(
                requireContext(),
                "Already recording activity automatically",
                Toast.LENGTH_LONG
            ).show()
        } else {
            if (!mStartActivityViewModel.isOngoingActivityRunning) {
                if (mStartActivityViewModel.selectedActivityTypeName.value != "-") {
                    val intent: Intent
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

                    intent.putExtra(
                        "activityType",
                        mStartActivityViewModel.selectedActivityTypeName.value
                    )
                    intent.putExtra("startTime", System.currentTimeMillis().toString())
                    intent.putExtra("firstTime", true.toString())

                    mStartActivityViewModel.isOngoingActivityRunning = true
                    startForResult.launch(intent)
                } else
                    Toast.makeText(
                        requireContext(),
                        "You need to choose an activity type",
                        Toast.LENGTH_SHORT
                    ).show()

            } else
                Toast.makeText(
                    requireContext(),
                    "An activity is already on",
                    Toast.LENGTH_SHORT
                ).show()
        }
    }
}