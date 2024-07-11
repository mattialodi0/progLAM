package com.example.proglam.ui.startActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.proglam.db.ActivityType

class StartActivityViewModel : ViewModel() {
    var autoRec: MutableLiveData<Boolean> = MutableLiveData(false)
    var isOngoingActivityRunning = false

    val selectedActivityTypeName = MutableLiveData("-")
    val selectedActivityType = MutableLiveData<ActivityType>()

    fun setSelectedActivityTypeName(activityType: String) {
        this.selectedActivityTypeName.postValue(activityType)
    }
    fun setSelectedActivityType(activityType: ActivityType) {
        this.selectedActivityType.postValue(activityType)
    }
}