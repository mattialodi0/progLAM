package com.example.proglam.db

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.Timestamp

class ActivityRecordViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ActivityRecordRepository
    val getActivityRecords: LiveData<List<ActivityRecord>>
    val getRecentActivityRecords: LiveData<List<ActivityRecord>>
    val getTodayActivitiesNumber: LiveData<Int>
    val fiveDaysActivities: LiveData<List<ActivityRecord>>
    // fatti bene
    var _getActivitiesFromTo: MutableLiveData<List<ActivityRecord>>
    var getActivitiesFromTo: LiveData<List<ActivityRecord>>
    var _getActivityById: MutableLiveData<ActivityRecord>
    var getActivityById: LiveData<ActivityRecord>


    init {
        val activityRecordDao = ActivityDatabase.getDatabase(application).activityRecordDao()
        repository = ActivityRecordRepository(activityRecordDao)
        getActivityRecords = repository.getActivityRecords
        getRecentActivityRecords = repository.getRecentActivityRecords
        getTodayActivitiesNumber = repository.getTodayActivitiesNumber
        fiveDaysActivities = repository.getFiveDaysActivities

        _getActivitiesFromTo = MutableLiveData(emptyList())
        getActivitiesFromTo = _getActivitiesFromTo
        _getActivityById = MutableLiveData()
        getActivityById = _getActivityById
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
}
