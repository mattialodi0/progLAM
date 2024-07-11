package com.example.proglam.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
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
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate


object MPChartBuilder {
    @SuppressLint("ResourceType")
    fun buildBarChart(context: Context, data: Array<Float>, labels: ArrayList<String>): BarChart {
        val barChart = BarChart(context)
        val list: ArrayList<BarEntry> = ArrayList()

        for (i in data.indices) {
            list.add(BarEntry((i + 2).toFloat(), data[i]))
        }

        val barDataset = BarDataSet(list, "List")
        barDataset.setColors(ColorTemplate.MATERIAL_COLORS, 255)

        if (System.isNightModeOn(context)) {
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
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels.toList())
        barChart.legend.isEnabled = false
        barChart.animateY(1000)

        return barChart
    }

    fun buildPieChart(context: Context, data: Array<Float>, labels: ArrayList<String>): PieChart {
        val pieChart: PieChart = PieChart(context)
        val list: ArrayList<PieEntry> = ArrayList()

        for (i in data.indices) {
            list.add(PieEntry(data[i], labels[i]))
        }

        val pieDataSet = PieDataSet(list, "List")
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS, 255)
        pieDataSet.valueTextSize = 15f
        pieDataSet.valueTextColor = Color.WHITE

        val pieData = PieData(pieDataSet)
        pieChart.data = pieData
        pieChart.description.text = " "
        pieChart.centerText = "List"
        pieChart.animateXY(
            1400,
            1400,
            Easing.EasingOption.EaseInOutQuad,
            Easing.EasingOption.EaseInOutQuad
        )


        val l: Legend = pieChart.legend
        l.isEnabled = false

        return pieChart
    }

    fun buildRadarChart(context: Context, data: Array<Float>, labels: ArrayList<String>): RadarChart {
        val radarChart: RadarChart = RadarChart(context)
        val list: ArrayList<RadarEntry> = ArrayList()

        for (i in data.indices) {
            list.add(RadarEntry(data[i]))
        }

        val radarDataSet = RadarDataSet(list, "List")
        radarDataSet.setColors(ColorTemplate.MATERIAL_COLORS, 255)
        radarDataSet.setDrawFilled(true)
        radarDataSet.valueTextSize = 0f
        radarDataSet.valueTextColor = Color.WHITE


        val radarData = RadarData(radarDataSet)
        radarChart.data = radarData
        radarChart.description.text = " "
        radarChart.webLineWidth = 1f
        radarChart.webColor = Color.WHITE
        radarChart.webLineWidthInner = 1f
        radarChart.webColorInner = Color.WHITE
        radarChart.webAlpha = 100
        radarChart.yAxis.setDrawLabels(false)       // optional
        radarChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)

        radarChart.animateXY(
            1400,
            1400,
            Easing.EasingOption.EaseInOutQuad,
            Easing.EasingOption.EaseInOutQuad
        )

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

        return radarChart
    }
}