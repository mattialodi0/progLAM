package com.example.proglam.utils

import com.example.proglam.db.ActivityRecord
import com.example.proglam.db.ActivityTypeWithActivityRecord
import com.example.proglam.db.TypeDailyTimeTuple
import com.example.proglam.db.TypeDurationTuple
import com.google.gson.Gson
import java.lang.System


object ProcessDBData {

    fun ARwithACtoList(it: List<Any>): Pair<Array<Float>, ArrayList<String>> {
        val a = it as List<ActivityTypeWithActivityRecord>
        var max = 0
        for (join in a) {
            if (join.activityType.id > max)
                max = join.activityType.id
        }
        val arr = ArrayList<Float>(max)
        for (i in 0..max) {
            arr.add(0F)
        }
        val labels = ArrayList<String>(max)
        for (i in 0..max) {
            labels.add(" ")
        }
        for (join in a) {
            arr[join.activityType.id] =
                arr[join.activityType.id] + (join.activityRecord.finishTime - join.activityRecord.startTime) / 1000F / 60F
            labels[join.activityType.id] = join.activityType.name
        }

        // removes the empty fields in the array
        var i = 0
        while (i <= max) {
            if (arr[i] == 0F) {
                arr.removeAt(i)
                labels.removeAt(i)
                max--
            } else i++
        }

        labels.add(0, " ")
        labels.add(0, " ")
        return Pair(arr.toFloatArray().toTypedArray(), labels)
    }

    fun ARStepsToList(it: List<Any>, timeRange: Int): Pair<Array<Float>, ArrayList<String>> {
        val a = it as List<ActivityRecord>

        var steps: ArrayList<Float>
        var labels: ArrayList<String> = arrayListOf()
        when (timeRange) {
            2 -> {
                steps = arrayListOf(0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F)
                //labels = arrayListOf("", "", "", "", "", "", "", "", "", "", "last month", "this month")
                for (ar in a) {
                    val i = 11 - ((System.currentTimeMillis() - ar.startTime) / (86400000L * 28))
                    steps[i.toInt()] =
                        steps[i.toInt()] + Gson().fromJson(ar.toolData, JsonData::class.java).steps
                }
            }

            1 -> {
                steps = arrayListOf(0F, 0F, 0F, 0F)
                //labels = arrayListOf("","", "last week", "this week")
                for (ar in a) {
                    val i = 3 - ((System.currentTimeMillis() - ar.startTime) / (86400000L * 7))
                    steps[i.toInt()] =
                        steps[i.toInt()] + Gson().fromJson(ar.toolData, JsonData::class.java).steps
                }
            }

            else -> {
                steps = arrayListOf(0F, 0F, 0F, 0F, 0F, 0F, 0F)
                labels = arrayListOf("", "", "", "", "", "yesterday", "today")
                for (ar in a) {
                    val i = 6 - ((System.currentTimeMillis() - ar.startTime) / 86400000L)
                    steps[i.toInt()] =
                        steps[i.toInt()] + Gson().fromJson(ar.toolData, JsonData::class.java).steps
                }
            }
        }

        labels.add(0, " ")
        labels.add(0, " ")
        return Pair(steps.toFloatArray().toTypedArray(), labels)
    }

    fun ARDayPercentageToList(it: List<Any>): Pair<Array<Float>, ArrayList<String>> {
        val tuple = it as List<TypeDailyTimeTuple>

        // init arrays
        var max = 0
        for (a in tuple) {
            if (a.id!! > max)
                max = a.id
        }
        val arr = ArrayList<Float>(max)
        val labels = ArrayList<String>(max)
        for (i in 0..max) {
            arr.add(0F)
        }

        // average by day
        var sum = 0F
        var n = 0
        for (i in 0..max) {
            for (a in tuple) {
                if (a.id == i) {
                    sum += a.dailyTime!!
                    n += 1
                }
            }
            arr[i] = sum / n / (3600 * 24)
            sum = 0F
            n = 0
        }

        // labels
        for (i in 0..max) {
            for (a in tuple) {
                if (a.id == i) {
                    labels.add(a.type!!)
                    break
                }
            }
        }

        // remove NaNs
        var j = 0
        var m = max
        var other = 0F
        while (j <= m) {
            if (arr[j].isNaN()) {
                arr.removeAt(j)
                m--
            } else if (arr[j] < 0.1) {
                other += arr[j]
                arr.removeAt(j)
                labels.removeAt(j)
                m--
            } else j++
        }

        // add other activities
        if (other > 0) {
            arr.add(other)
            labels.add("other")
        }

        val formattedArr = ArrayList<Float>(5)
        for(a in arr) {
            formattedArr.add((a*100).toInt().toFloat())
        }

        return Pair(formattedArr.toTypedArray(), labels)

    }

    fun ARVehiclesToList(it: List<Any>): Pair<Array<Float>, ArrayList<String>> {
        val tuple = it as List<TypeDurationTuple>

        val arr = arrayListOf(0F, 0F, 0F)
        val labels = arrayListOf("walk", "run", "vehicles")
        var sum = 0F
        for (a in tuple) {
            when(a.type) {
                "walk" -> {
                    arr[0] = a.duration!!.toFloat()
                    sum += a.duration!!.toFloat()
                }
                "run" -> {
                    arr[1] = a.duration!!.toFloat()
                    sum += a.duration!!.toFloat()
                }
                "in vehicle" -> {
                    arr[2] = a.duration!!.toFloat()
                    sum += a.duration!!.toFloat()
                }
            }
        }

        // transform to percentage
        arr[1] = arr[1]/sum*100
        arr[0] = arr[0]/sum*100
        arr[2] = arr[2]/sum*100

        return Pair(arr.toFloatArray().toTypedArray(), labels)
    }
}