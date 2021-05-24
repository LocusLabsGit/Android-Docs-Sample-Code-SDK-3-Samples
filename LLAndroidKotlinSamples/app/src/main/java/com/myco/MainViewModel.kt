package com.myco

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

const val LAX_VENUE_ID = "lax"
const val LAX_GATE_60_POI_ID = "493"
const val LAX_GATE_60B_POI_ID = "1205"

class MainViewModel : ViewModel() {
    val venueID = LAX_VENUE_ID
    val poiID = LAX_GATE_60_POI_ID

    val showMarkerTappedFragment = MutableLiveData(false)
}