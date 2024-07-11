package com.example.proglam.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ActivityTypeViewModel(application: Application): AndroidViewModel(application) {
    private val repository: ActivityTypeRepository
    val getActivityTypes: LiveData<List<ActivityType>>
    private val _getActivityTypeByName: MutableLiveData<ActivityType>
    val getActivityTypeByName: LiveData<ActivityType>

    init {
        val activityTypeDao = ActivityDatabase.getDatabase(application).activityTypeDao()
        repository = ActivityTypeRepository(activityTypeDao)
        getActivityTypes = repository.getActivityTypes

        _getActivityTypeByName = MutableLiveData()
        getActivityTypeByName = _getActivityTypeByName
    }

    fun addActivityType(activityType: ActivityType) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addActivityType(activityType)
        }
    }
    fun removeActivityType(activityType: ActivityType) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeActivityType(activityType)
        }
    }

    fun findActivityTypeByName(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _getActivityTypeByName.postValue(repository.findActivityTypeByName(name))
        }
    }
}