package com.example.proglam.ui.newActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NewActivityViewModel: ViewModel() {
    var acName = MutableLiveData("")

    var acDescription = MutableLiveData("")
}