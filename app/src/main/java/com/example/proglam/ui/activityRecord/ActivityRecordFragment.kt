package com.example.proglam.ui.activityRecord

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.proglam.R
import com.example.proglam.db.ActivityRecordViewModel
import com.example.proglam.db.ActivityTypeViewModel
import com.example.proglam.utils.JsonData
import com.example.proglam.utils.Strings
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale


class ActivityRecordFragment : Fragment() {

    private val callback = OnMapReadyCallback { googleMap ->
        try {
            if (toolsObj == null)
                toolsObj = JsonData(arrayListOf(), 0)

            if (toolsObj.positions != null) {
                val update = CameraUpdateFactory.newLatLngZoom(toolsObj.positions[0], 16f)
                googleMap.addMarker(MarkerOptions().position(toolsObj.positions[0]).title("geolocation").icon(
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
                googleMap.moveCamera(update)
                googleMap.animateCamera(update)
            }
            if(toolsObj.positions.size > 1){
                val options = PolylineOptions().width(5f).color(Color.RED).geodesic(true)
                for (p in toolsObj.positions) {
                    options.add(p)
                }
                googleMap.addPolyline(options)
                val update = CameraUpdateFactory.newLatLngZoom(toolsObj.positions[0], 16f)
                googleMap.moveCamera(update)
                googleMap.animateCamera(update)
            }
        } catch (e: NullPointerException) {
            Log.i("ActivityRecordFragment", "NullPtr Exception in map callback")
        }
    }

    private val mActivityRecordViewModel: ActivityRecordViewModel by viewModels()
    private val mActivityTypeViewModel: ActivityTypeViewModel by viewModels()
    private var toolsObj: JsonData = JsonData(arrayListOf(), 0)

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

    @SuppressLint("DiscouragedApi")
    private fun setObservers(view: View) {
        mActivityRecordViewModel.getActivityById.observe(viewLifecycleOwner) { activityRecord ->
            mActivityTypeViewModel.findActivityTypeByName(activityRecord.type)

            val title = view.findViewById<TextView>(R.id.arType_tv)
            title.text = activityRecord.type
            val time = view.findViewById<TextView>(R.id.arTime_tv)
            val interval = (activityRecord.finishTime - activityRecord.startTime) / 1000
            time.text = Strings.formattedTimer(interval)
            val date = view.findViewById<TextView>(R.id.arDate_tv)
            date.text = SimpleDateFormat("dd/M/yyyy", Locale.ITALY).format(activityRecord.startTime)


            try {
                toolsObj = Gson().fromJson(activityRecord.toolData, JsonData::class.java)
                if (toolsObj == null)
                    toolsObj = JsonData(arrayListOf(), 0)

                if (toolsObj.positions != null && toolsObj.positions.isNotEmpty()) {
                    val mapView = view.findViewById<FragmentContainerView>(R.id.ar_map)
                    mapView.visibility = View.VISIBLE
                    val mapFragment =
                        childFragmentManager.findFragmentById(R.id.ar_map) as SupportMapFragment?
                    mapFragment?.getMapAsync(callback)
                    val tv = view.findViewById<TextView>(R.id.arMap_tv)
                    tv.visibility = View.VISIBLE
                }

                if (toolsObj == null)
                    toolsObj = JsonData(arrayListOf(), 0)

                if (toolsObj.steps > 0) {
                    val tv = view.findViewById<TextView>(R.id.arSteps_tv)
                    tv.visibility = View.VISIBLE
                    tv.text = String.format(resources.getString(R.string.steps_num), toolsObj.steps)
                }
            } catch (e: NullPointerException) {
                Log.i("ActivityRecordFragment", "NullPtr Exception extracting JSON data")
            }
        }

        mActivityTypeViewModel.getActivityTypeByName.observe(viewLifecycleOwner) { activityType ->
            val icon = view.findViewById<ImageView>(R.id.arIcon_iv)
            val iconSrc = if(activityType != null) activityType.iconSrc else "ic_activitytype_generic"
            icon.setImageDrawable(
                context?.resources?.let {
                    ContextCompat.getDrawable(
                        requireContext(),
                        it.getIdentifier(
                            iconSrc,
                            "drawable",
                            requireContext().packageName
                        )
                    )
                }
            )
        }
    }
}


