package com.example.proglam.ui.dashboard

import android.R.attr.bottom
import android.R.attr.left
import android.R.attr.right
import android.R.attr.textAlignment
import android.R.attr.top
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.view.View.TEXT_ALIGNMENT_VIEW_END
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.proglam.R
import com.example.proglam.databinding.FragmentDashboardBinding
import com.example.proglam.db.ActivityRecordViewModel
import com.example.proglam.utils.Strings
import com.example.proglam.utils.System
import com.example.proglam.utils.TextDrawable
import com.github.mikephil.charting.utils.Utils
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.MaterialColors


class DashboardFragment : Fragment() {
    //val mDashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
    private val mActivityRecordViewModel: ActivityRecordViewModel by viewModels()

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Fill the view with data
        addButtonListeners(root)
        displayTodayActivitiesNumber(root)
        populateActivityList(root)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Function for the UI
    private fun addButtonListeners(view: View) {
        val newActivityBtn: Button = view.findViewById(R.id.newActivity_btn)!!
        newActivityBtn.setOnClickListener {
            findNavController().navigate(R.id.navigation_newActivity)
        }
    }

    private fun displayTodayActivitiesNumber(view: View) {
        mActivityRecordViewModel.getTodayActivitiesNumber.observe(
            viewLifecycleOwner,
            Observer { todayActivities ->
                val todayActivitiesNumBtn: MaterialButton =
                    view.findViewById(R.id.todayActivitiesNum_btn)
                val drawableNum = TextDrawable(
                    todayActivities.toString(),
                    Color.WHITE,
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_SP,
                        24F,
                        resources.displayMetrics
                    )
                )
                todayActivitiesNumBtn.icon = drawableNum
            }
        )
    }

    private fun populateActivityList(view: View) {
        var i = 0
        val dm = resources.displayMetrics
        val dpInPx16 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16F, dm).toInt()

        val ll: LinearLayout = view.findViewById(R.id.activityRecords_ll)
        ll.removeAllViews()

        mActivityRecordViewModel.getRecentActivityRecords.observe(
            viewLifecycleOwner,
            Observer { activityRecords ->
                for (activityRecord in activityRecords) {
                    if (i < activityRecords.size) {
                        val arBtn = MaterialButton(requireContext())
                        arBtn.cornerRadius = dpInPx16
                        arBtn.setBackgroundColor(MaterialColors.getColor(
                            requireContext(),
                            R.attr.colorOnBackground,
                            Color.GRAY
                        ))
                        arBtn.text = "${activityRecord.type} - ${Strings.formattedTimer((activityRecord.finishTime-activityRecord.startTime) / 1000)}"
                        if(System.isNightModeOn(requireContext()))
                            arBtn.setTextColor(Color.GRAY)
                        else
                            arBtn.setTextColor(Color.WHITE)
                        arBtn.isAllCaps = false

                        arBtn.setOnClickListener {
                            val bundle = Bundle()
                            bundle.putString("arId", activityRecord.id.toString())
                            findNavController().navigate(R.id.navigation_activityRecord, bundle)
                        }

                        ll.addView(arBtn)
                    }
                    i++
                }
            }
        )
    }
}