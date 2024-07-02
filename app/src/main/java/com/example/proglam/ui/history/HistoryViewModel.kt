package com.example.proglam.ui.history

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HistoryViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is history Fragment"
    }
    val text: LiveData<String> = _text

    var timeRange = MutableLiveData<String>("by month")
    var chartType = MutableLiveData<Int?>(null)

    fun changeChartType() {
        val n = chartType.value?.plus(1)
        if (n != null) {
            if(n <= 3)
                chartType.postValue(n)
            else
                chartType.postValue(0)
        }
        else {
            chartType.postValue(1)
        }
    }
}