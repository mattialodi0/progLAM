package com.example.proglam.ui.dashboard

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proglam.R
import com.example.proglam.databinding.FragmentDashboardBinding
import com.example.proglam.db.ActivityRecordViewModel
import com.example.proglam.list.ARRecyclerviewAdapter
import com.example.proglam.list.ARRecyclerviewInterface
import com.example.proglam.utils.TextDrawable
import com.google.android.material.button.MaterialButton


class DashboardFragment : Fragment(), ARRecyclerviewInterface {
    private val mActivityRecordViewModel: ActivityRecordViewModel by viewModels()

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = root.findViewById<RecyclerView>(R.id.recentAR_rv)
        val adapter = ARRecyclerviewAdapter(
            this,
            mActivityRecordViewModel.getRecentActivityRecords,
            viewLifecycleOwner
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        addButtonListeners(root)
        displayTodayActivitiesNumber(root)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Function for the UI
    private fun addButtonListeners(view: View) {
        val newActivityBtn: Button = view.findViewById(R.id.newActivity_btn)
        newActivityBtn.setOnClickListener {
            findNavController().navigate(R.id.navigation_newActivity)
        }
        val todayActivitiesBtn: Button = view.findViewById(R.id.todayActivitiesNum_btn)
        todayActivitiesBtn.setOnClickListener {
            findNavController().navigate(R.id.navigation_calendarActivity)
        }
    }

    private fun displayTodayActivitiesNumber(view: View) {
        mActivityRecordViewModel.getTodayActivitiesNumber.observe(viewLifecycleOwner)
        { todayActivities ->
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

    }

    override fun onItemClick(pos: Int) {
        val ars = mActivityRecordViewModel.getRecentActivityRecords.value
        if (ars != null) {
            val bundle = Bundle()
            bundle.putString("arId", ars[pos].id.toString())
            findNavController().navigate(R.id.navigation_activityRecord, bundle)
        }
    }
}