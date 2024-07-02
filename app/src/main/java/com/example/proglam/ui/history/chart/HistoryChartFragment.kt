package com.example.proglam.ui.history.chart

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels
import com.example.proglam.R
import com.example.proglam.ui.history.HistoryViewModel
import com.example.proglam.utils.System
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.utils.ColorTemplate


class HistoryChartFragment : Fragment() {
    private val mHistoryViewModel:HistoryViewModel by navGraphViewModels(R.id.navigation_history)

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
            when (it) {
                null -> {
                    drawBarChart(view, arrayOf(0f, 10f, 20f, 30f))
                }
                0 -> {
                    removeChart(view)
                    drawBarChart(view, arrayOf(0f, 10f, 20f, 30f))
                }
                1 -> {
                    removeChart(view)
                    drawBarChart(view, arrayOf(0f, 10f, 20f, 30f))
                }
                2 -> {
                    removeChart(view)
                    drawPieChart(view, arrayOf(0f, 10f, 20f, 30f))
                }
                3 -> {
                    removeChart(view)
                    drawRadarChart(view, arrayOf(0f, 10f, 20f, 30f))
                }
            }
        }
    }

    private fun removeChart(view: View) {
        val container: FrameLayout = view.findViewById(R.id.historyChart_fl)
        container.removeAllViews()
    }

    @SuppressLint("ResourceType")
    private fun drawBarChart(view: View, data: Array<Float>) {
        val barChart: BarChart = BarChart(context)
        val list: ArrayList<BarEntry> = ArrayList()

        for(i in data.indices) {
            list.add(BarEntry((i+2).toFloat(), data[i]))
        }

        val barDataset = BarDataSet(list, "List")
        barDataset.setColors(ColorTemplate.COLORFUL_COLORS, 255)

        if (System.isNightModeOn(requireContext())) {
            barDataset.valueTextColor = Color.LTGRAY
            barChart.axisLeft.textColor = Color.LTGRAY
            barChart.xAxis.textColor = Color.LTGRAY
            barChart.xAxis.axisLineColor = Color.LTGRAY
        } else {
            barDataset.valueTextColor = Color.WHITE
            barChart.axisLeft.textColor = Color.WHITE
            barChart.xAxis.textColor = Color.WHITE
            barChart.xAxis.axisLineColor = Color.WHITE
        }

        val barData = BarData(barDataset)
        barChart.setFitBars(true)
        barChart.data = barData
        barChart.description.text = " "
        barChart.axisRight.setDrawLabels(false)
        barChart.xAxis.setDrawLabels(false)
        barChart.legend.isEnabled = false
        barChart.animateY(1000)

        val container: FrameLayout = view.findViewById(R.id.historyChart_fl)
        container.addView(barChart)
    }

    @SuppressLint("ResourceType")
    private fun drawPieChart(view: View, data: Array<Float>) {
        val pieChart: PieChart = PieChart(context)
        val list: ArrayList<PieEntry> = ArrayList()

        for(i in data.indices) {
            list.add(PieEntry(data[i], i.toString()))
        }

        val pieDataSet = PieDataSet(list, "List")
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS, 255)
        pieDataSet.valueTextSize=15f
        pieDataSet.valueTextColor= Color.WHITE

        val pieData = PieData(pieDataSet)
        pieChart.data = pieData
        pieChart.description.text = " "
        pieChart.centerText="List"
        pieChart.animateXY(1400, 1400, Easing.EasingOption.EaseInOutQuad, Easing.EasingOption.EaseInOutQuad)


        val l: Legend = pieChart.legend
        l.isEnabled = false

        val container: FrameLayout = view.findViewById(R.id.historyChart_fl)
        container.addView(pieChart)
    }

    @SuppressLint("ResourceType")
    private fun drawRadarChart(view: View, data: Array<Float>) {
        val radarChart: RadarChart = RadarChart(context)
        val list: ArrayList<RadarEntry> = ArrayList()

        for(i in data.indices) {
            list.add(RadarEntry(data[i]))
        }

        val radarDataSet = RadarDataSet(list, "List")
        radarDataSet.setColors(ColorTemplate.COLORFUL_COLORS, 255)
        radarDataSet.setDrawFilled(true)
        radarDataSet.valueTextSize=0f
        radarDataSet.valueTextColor= Color.WHITE


        val radarData = RadarData(radarDataSet)
        radarChart.data = radarData
        radarChart.description.text = " "
        radarChart.webLineWidth = 1f;
        radarChart.webColor = Color.WHITE;
        radarChart.webLineWidthInner = 1f;
        radarChart.webColorInner = Color.WHITE;
        radarChart.webAlpha = 100;
        radarChart.animateXY(1400, 1400, Easing.EasingOption.EaseInOutQuad, Easing.EasingOption.EaseInOutQuad)

        val xAxis: XAxis = radarChart.xAxis
        xAxis.setTextSize(9f)
        xAxis.yOffset = 0f
        xAxis.xOffset = 0f
        xAxis.textColor = Color.WHITE

        val yAxis: YAxis = radarChart.yAxis
        yAxis.setLabelCount(5, false)
        yAxis.setTextSize(9f)
        yAxis.setAxisMinimum(0f)
        yAxis.textColor = Color.WHITE

        val l: Legend = radarChart.legend
        l.isEnabled = false

        val container: FrameLayout = view.findViewById(R.id.historyChart_fl)
        container.addView(radarChart)
    }
}


