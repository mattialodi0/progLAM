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
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.util.Date


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

            val secondsPerDay = arrayOf(0L,0L,0L,0L,0L)
            val dayLength:Long = 86400000
            val today = Date(Date().time - Date().time % (24 * 60 * 60 * 1000)).time

            for(ar in activitiesRecords) {
                if (ar.startTime > today) {
                    secondsPerDay[4] += (ar.finishTime-ar.startTime)/1000/60
                }
                else if (ar.startTime > today - 1*dayLength) {
                    secondsPerDay[3] += (ar.finishTime-ar.startTime)/1000/60
                }
                else if (ar.startTime > today - 2*dayLength) {
                    secondsPerDay[2] += (ar.finishTime-ar.startTime)/1000/60
                }
                else if (ar.startTime > today - 3*dayLength) {
                    secondsPerDay[1] += (ar.finishTime-ar.startTime)/1000/60
                }
                else if (ar.startTime > today - 4*dayLength) {
                    secondsPerDay[0] += (ar.finishTime-ar.startTime)/1000/60
                }
            }
            drawBarChart(view, secondsPerDay)
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
            barChart.description.textColor = Color.LTGRAY
        }
        else {
            barDataset.valueTextColor = Color.WHITE
            barChart.axisLeft.textColor = Color.WHITE
            barChart.xAxis.textColor = Color.WHITE
            barChart.xAxis.axisLineColor = Color.WHITE
            barChart.description.textColor = Color.WHITE
        }

        var barData = BarData(barDataset)
        barChart.setFitBars(true)
        barChart.data = barData
        barChart.description.text = "minutes per day"
        barChart.axisRight.setDrawLabels(false)
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(listOf("","","","","","yesterday","today"))
        barChart.legend.isEnabled = false
        barChart.animateY(2000)
    }

    private fun getMinutesByDay(): Array<Long> {
        return arrayOf((0..60*24).random().toLong(),(0..60*24).random().toLong(),(0..60*24).random().toLong(),(0..60*24).random().toLong(),(0..60*24).random().toLong())
    }
}