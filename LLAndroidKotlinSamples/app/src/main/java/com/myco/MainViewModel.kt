package com.myco

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val showMarkerTappedFragment = MutableLiveData(false)
}