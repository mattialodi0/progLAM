package com.example.proglam.ui.history.chart

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.navGraphViewModels
import com.example.proglam.R
import com.example.proglam.db.ActivityRecordViewModel
import com.example.proglam.ui.history.HistoryViewModel
import com.example.proglam.utils.MPChartBuilder.buildBarChart
import com.example.proglam.utils.MPChartBuilder.buildPieChart
import com.example.proglam.utils.MPChartBuilder.buildRadarChart
import com.example.proglam.utils.ProcessDBData
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.charts.RadarChart


class HistoryChartFragment : Fragment() {
    private val mHistoryViewModel: HistoryViewModel by navGraphViewModels(R.id.navigation_history)
    private val mActivityRecordViewModel: ActivityRecordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history_chart, container, false)

        setObserver(view)

        return view
    }

    private fun setObserver(view: View) {
        mHistoryViewModel.chartType.observe(viewLifecycleOwner) {
            val timeRange = getTimeRangeInt(mHistoryViewModel.timeRange.value!!)
            if (it == null)
                mActivityRecordViewModel.findActivitiesForCharts(0, timeRange)
            else
                mActivityRecordViewModel.findActivitiesForCharts(it, timeRange)
        }

        mHistoryViewModel.timeRange.observe(viewLifecycleOwner) {
            val timeRange = getTimeRangeInt(it)
            if (mHistoryViewModel.chartType.value == null)
                mActivityRecordViewModel.findActivitiesForCharts(0, timeRange)
            else
                mActivityRecordViewModel.findActivitiesForCharts(mHistoryViewModel.chartType.value!!, timeRange)
        }

        mActivityRecordViewModel.getActivitiesForCharts.observe(viewLifecycleOwner) {
            if (it.isEmpty())
                return@observe

            when (mHistoryViewModel.chartType.value) {
                null -> {
                    val data = ProcessDBData.ARwithACtoList(it!!)
                    drawBarChart(view, data.first, data.second)
                }
                0 -> {
                    removeChart(view)
                    val data = ProcessDBData.ARwithACtoList(it!!)
                    drawBarChart(view, data.first, data.second)
                }
                1 -> {
                    removeChart(view)
                    val data = ProcessDBData.ARStepsToList(it!!, getTimeRangeInt(mHistoryViewModel.timeRange.value!!))
                    drawBarChart(view, data.first, data.second)
                }
                2 -> {
                    removeChart(view)
                    val data = ProcessDBData.ARDayPercentageToList(it!!)
                    drawPieChart(view, data.first, data.second)
                }
                3 -> {
                    removeChart(view)
                    val data = ProcessDBData.ARVehiclesToList(it!!)
                    drawRadarChart(view, data.first, data.second)
                }
            }
        }
    }

    private fun getTimeRangeInt(time: String): Int {
        return when (time) {
            "last week" -> 0
            "last month" -> 1
            else -> 2
        }
    }

    private fun removeChart(view: View) {
        val container: FrameLayout = view.findViewById(R.id.historyChart_fl)
        container.removeAllViews()
    }

    @SuppressLint("ResourceType")
    private fun drawBarChart(view: View, data: Array<Float>, labels: ArrayList<String>) {
        val barChart: BarChart = buildBarChart(requireContext(), data, labels)

        val container: FrameLayout = view.findViewById(R.id.historyChart_fl)
        container.addView(barChart)
    }

    @SuppressLint("ResourceType")
    private fun drawPieChart(view: View, data: Array<Float>, labels: ArrayList<String>) {
        val pieChart: PieChart = buildPieChart(requireContext(), data, labels)

        val container: FrameLayout = view.findViewById(R.id.historyChart_fl)
        container.addView(pieChart)
    }

    @SuppressLint("ResourceType")
    private fun drawRadarChart(view: View, data: Array<Float>, labels: ArrayList<String>) {
        val radarChart: RadarChart = buildRadarChart(requireContext(), data, labels)

        val container: FrameLayout = view.findViewById(R.id.historyChart_fl)
        container.addView(radarChart)
    }
}


