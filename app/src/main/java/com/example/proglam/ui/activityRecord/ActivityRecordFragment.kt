package com.example.proglam.ui.activityRecord

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.proglam.R
import com.example.proglam.background.activityServices.GpsService
import com.example.proglam.db.ActivityRecordViewModel
import com.example.proglam.db.ActivityTypeViewModel
import com.example.proglam.utils.JsonData
import com.example.proglam.utils.Strings

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class ActivityRecordFragment : Fragment() {

        private val callback = OnMapReadyCallback { googleMap ->
            val lat = toolsObj.positions[0].first
            val lng = toolsObj.positions[0].second
            val myPos = LatLng(lat, lng)
            googleMap.addMarker(MarkerOptions().position(myPos).title("myMarker"))
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(myPos))
            googleMap.animateCamera( CameraUpdateFactory.zoomTo( 16.0f ) );
        }


    private val mActivityRecordViewModel: ActivityRecordViewModel by viewModels()
    private val mActivityTypeViewModel: ActivityTypeViewModel by viewModels()
    private lateinit var toolsObj: JsonData


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var activityRecordId = arguments?.getString("arId")?.toInt()
        if (activityRecordId == null) {
            activityRecordId = 1
        }
        mActivityRecordViewModel.findActivityById(activityRecordId)

        val view = inflater.inflate(R.layout.fragment_activity_record, container, false)
        setObservers(view)
        setListener(view)

        return view
    }

    private fun setListener(view: View) {
        val deleteBtn = view.findViewById<FloatingActionButton>(R.id.arDelete_btn)
        deleteBtn.setOnClickListener {
            arguments?.getString("arId")?.toInt()
                ?.let { it1 -> mActivityRecordViewModel.deleteActivityRecord(it1) }
            Toast.makeText(requireContext(), "Activity deleted", Toast.LENGTH_LONG).show()
            findNavController().popBackStack()
        }
    }

    private fun setObservers(view: View) {
        mActivityRecordViewModel.getActivityById.observe(viewLifecycleOwner) { activityRecord ->
            mActivityTypeViewModel.findActivityTypeByName(activityRecord.type)

            val title = view.findViewById<TextView>(R.id.arType_tv)
            title.text = activityRecord.type
            val time = view.findViewById<TextView>(R.id.arTime_tv)
            val interval = (activityRecord.finishTime-activityRecord.startTime) / 1000
            time.text = Strings.formattedTimer(interval)
            val date = view.findViewById<TextView>(R.id.arDate_tv)
            date.text = SimpleDateFormat("dd/M/yyyy", Locale.ITALY).format(activityRecord.startTime)


            toolsObj = Gson().fromJson(activityRecord.toolData, JsonData::class.java)
            val mapFragment = childFragmentManager.findFragmentById(R.id.ar_map) as SupportMapFragment?
            mapFragment?.getMapAsync(callback)

            val info = view.findViewById<TextView>(R.id.arInfo_tv)
            info.text = "steps: ${toolsObj.steps}"
        }

        mActivityTypeViewModel.getActivityTypeByName.observe(viewLifecycleOwner) { activityType ->
            val icon = view.findViewById<ImageView>(R.id.arIcon_iv)
            icon.setImageDrawable(
                context?.resources?.let {
                    ContextCompat.getDrawable(
                        requireContext(),
                        it.getIdentifier(
                            activityType.iconSrc.toString(),
                            "drawable",
                            requireContext().packageName
                        )
                    )
                }
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //val mapFragment = childFragmentManager.findFragmentById(R.id.activityRecord_map) as SupportMapFragment?
        //mapFragment?.getMapAsync(callback)
    }
}