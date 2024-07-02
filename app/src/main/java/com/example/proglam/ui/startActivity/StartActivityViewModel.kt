package com.example.proglam.ui.startActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.proglam.db.ActivityType

class StartActivityViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is start activity Fragment"
    }
    val text: LiveData<String> = _text


    val selectedActivityTypeName = MutableLiveData("-")
    val selectedActivityType = MutableLiveData<ActivityType>()


    fun setSelectedActivityTypeName(activityType: String) {
        this.selectedActivityTypeName.postValue(activityType)
    }
    fun setSelectedActivityType(activityType: ActivityType) {
        this.selectedActivityType.postValue(activityType)
    }
}