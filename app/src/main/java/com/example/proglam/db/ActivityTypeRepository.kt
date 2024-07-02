package com.example.proglam.db

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ActivityTypeRepository(private val activityTypeDao: ActivityTypeDao) {

    val getActivityTypes: LiveData<List<ActivityType>> = activityTypeDao.getActivityTypes()

    suspend fun addActivityType(activityType: ActivityType) {
        activityTypeDao.addActivityType(activityType)
    }

    suspend fun removeActivityType(activityType: ActivityType) {
        activityTypeDao.removeActivityType(activityType)
    }

    suspend fun findActivityTypeByName(name: String): ActivityType {
        return activityTypeDao.findActivityTypeByName(name)
    }
}