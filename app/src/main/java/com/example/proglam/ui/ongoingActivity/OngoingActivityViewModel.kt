package com.example.proglam.ui.ongoingActivity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OngoingActivityViewModel: ViewModel() {

    val activityType = MutableLiveData<String>("-")
}