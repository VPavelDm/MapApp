package com.vpaveldm.mapapp.viewModel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.vpaveldm.mapapp.model.Marker
import com.google.android.gms.maps.model.Marker as GoogleMarker

class MapViewModel : ViewModel(), IMarkerManager {

    override fun error(message: String) {
        errorLiveData.value = message
    }

    override fun addedMarker(marker: Marker): Boolean {
        if (markers.size == 2) return false
        markers.add(marker)
        markerLiveData.value = marker
        return true
    }

    var lastSelectedMarker: GoogleMarker? = null
    val markers: ArrayList<Marker> = arrayListOf()
    val markerLiveData: MutableLiveData<Marker> = MutableLiveData()
    val errorLiveData: MutableLiveData<String> = MutableLiveData()
}