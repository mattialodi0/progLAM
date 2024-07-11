package com.example.proglam.ui.history

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HistoryViewModel : ViewModel() {
    var timeRange = MutableLiveData("last month")
    private var _chartType = MutableLiveData<Int?>(null)
    val chartType = _chartType

    fun changeChartType() {
        val n = _chartType.value?.plus(1)
        if (n != null) {
            if(n <= 3)
                _chartType.postValue(n)
            else
                _chartType.postValue(0)
        }
        else {
            _chartType.postValue(1)
        }
    }
}