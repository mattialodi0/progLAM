package com.example.proglam.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.proglam.R
import com.example.proglam.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val mHistoryViewModel:HistoryViewModel by navGraphViewModels(R.id.navigation_history)

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.viewmodel = mHistoryViewModel
        mHistoryViewModel.timeRange.postValue("last month")

        setButtonListeners(root)
        setObservers(root)

        return root
    }

    override fun onResume() {
        super.onResume()

        val timeRanges = resources.getStringArray(R.array.time_ranges)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, timeRanges)
        binding.autoCompleteTextView.setAdapter(arrayAdapter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Function for the UI
    private fun setButtonListeners(view: View) {
        val searchBtn: Button = view.findViewById(R.id.search_btn)
        searchBtn.setOnClickListener {
            findNavController().navigate(R.id.navigation_calendarActivity)
        }

        val chartBtn: Button = view.findViewById(R.id.changeStats_btn)
        chartBtn.setOnClickListener {
            mHistoryViewModel.changeChartType()
        }
    }
    private fun setObservers(view: View) {
        mHistoryViewModel.timeRange.observe(viewLifecycleOwner, Observer {timeRange ->
        })

        mHistoryViewModel.chartType.observe(viewLifecycleOwner) {
            when (it) {
                null -> {
                    view.findViewById<TextView>(R.id.chartTitle_tv).text = "minutes"
                }
                0 -> {
                    view.findViewById<TextView>(R.id.chartTitle_tv).text = "minutes"
                }
                1 -> {
                    view.findViewById<TextView>(R.id.chartTitle_tv).text = "steps"
                }
                2 -> {
                    view.findViewById<TextView>(R.id.chartTitle_tv).text = "activities %"
                }
                3 -> {
                    view.findViewById<TextView>(R.id.chartTitle_tv).text = "movement"
                }
            }
        }

    }

}