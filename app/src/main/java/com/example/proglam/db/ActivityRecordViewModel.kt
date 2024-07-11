package com.example.proglam.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ActivityRecordViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ActivityRecordRepository
    val getRecentActivityRecords: LiveData<List<ActivityRecord>>
    val getTodayActivitiesNumber: LiveData<Int>
    val fiveDaysActivities: LiveData<List<ActivityRecord>>

    private var _getActivitiesFromTo: MutableLiveData<List<ActivityRecord>>
    var getActivitiesFromTo: LiveData<List<ActivityRecord>>
    private var _getActivityById: MutableLiveData<ActivityRecord>
    var getActivityById: LiveData<ActivityRecord>
    private var _getActivitiesForCharts: MutableLiveData<List<Any>>
    var getActivitiesForCharts: LiveData<List<Any>>


    init {
        val activityRecordDao = ActivityDatabase.getDatabase(application).activityRecordDao()
        repository = ActivityRecordRepository(activityRecordDao)
        getRecentActivityRecords = repository.getRecentActivityRecords
        getTodayActivitiesNumber = repository.getTodayActivitiesNumber
        fiveDaysActivities = repository.getFiveDaysActivities

        _getActivitiesFromTo = MutableLiveData(emptyList())
        getActivitiesFromTo = _getActivitiesFromTo
        _getActivityById = MutableLiveData()
        getActivityById = _getActivityById
        _getActivitiesForCharts = MutableLiveData(emptyList())
        getActivitiesForCharts = _getActivitiesForCharts
    }

    fun addActivityRecord(activityRecord: ActivityRecord) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addActivityRecord(activityRecord)
        }
    }
    fun deleteActivityRecord(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteActivityRecord(id)
        }
    }

    fun findActivitiesFromTo(from: Long, to: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _getActivitiesFromTo.postValue(repository.getActivitiesFromTo(from, to))
        }
    }

    fun findActivityById(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _getActivityById.postValue(repository.getActivityById(id))
        }
    }

    fun findActivitiesForCharts(type: Int, timeRange: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _getActivitiesForCharts.postValue(repository.findActivitiesForCharts(type, timeRange))
        }
    }
}
