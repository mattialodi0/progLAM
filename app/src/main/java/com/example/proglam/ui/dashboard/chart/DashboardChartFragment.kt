package com.example.proglam.ui.dashboard.chart

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.proglam.MainActivity
import com.example.proglam.R
import com.example.proglam.db.ActivityRecordViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate


class DashboardChartFragment : Fragment() {
    private val mActivityRecordViewModel: ActivityRecordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard_graph, container, false)

        setObserver(view)

        return view
    }

    private fun setObserver(view: View) {
        mActivityRecordViewModel.fiveDaysActivities.observe(viewLifecycleOwner) {  activitiesRecords ->

            val minutesPerDay = arrayOf(0L,0L,0L,0L,0L)
            val dayLength:Long = 86400000
            val timeNow: Long = System.currentTimeMillis()

            for(ar in activitiesRecords) {
                if (ar.startTime < timeNow - 4*dayLength) {
                    minutesPerDay[0] += ar.finishTime-ar.startTime
                }
                else if (ar.startTime < timeNow - 3*dayLength) {
                    minutesPerDay[1] += ar.finishTime-ar.startTime
                }
                else if (ar.startTime < timeNow - 2*dayLength) {
                    minutesPerDay[2] += ar.finishTime-ar.startTime
                }
                else if (ar.startTime < timeNow - 1*dayLength) {
                    minutesPerDay[3] += ar.finishTime-ar.startTime
                }
                else if (ar.startTime < timeNow) {
                    minutesPerDay[4] += ar.finishTime-ar.startTime
                }
            }

            drawBarChart(view, minutesPerDay)
        }
    }

    @SuppressLint("ResourceType")
    private fun drawBarChart(view: View, data: Array<Long>) {
        val barChart: BarChart = view.findViewById(R.id.bar_chart)
        var list : ArrayList<BarEntry> = ArrayList()

        for(i in data.indices) {
            list.add(BarEntry((i+2).toFloat(), data[i].toFloat()))
        }

        val barDataset = BarDataSet(list, "List")
        barDataset.setColors(ColorTemplate.COLORFUL_COLORS, 255)

        if (com.example.proglam.utils.System.isNightModeOn(requireContext())) {
            barDataset.valueTextColor = Color.LTGRAY
            barChart.axisLeft.textColor = Color.LTGRAY
            barChart.xAxis.textColor = Color.LTGRAY
            barChart.xAxis.axisLineColor = Color.LTGRAY
        }
        else {
            barDataset.valueTextColor = Color.WHITE
            barChart.axisLeft.textColor = Color.WHITE
            barChart.xAxis.textColor = Color.WHITE
            barChart.xAxis.axisLineColor = Color.WHITE
        }

        var barData = BarData(barDataset)
        barChart.setFitBars(true)
        barChart.data = barData
        barChart.description.text=" "
        barChart.axisRight.setDrawLabels(false)
        barChart.xAxis.setDrawLabels(false)
        barChart.legend.isEnabled = false
        barChart.animateY(2000)
    }

    private fun getMinutesByDay(): Array<Long> {
        return arrayOf((0..60*24).random().toLong(),(0..60*24).random().toLong(),(0..60*24).random().toLong(),(0..60*24).random().toLong(),(0..60*24).random().toLong())
    }
}