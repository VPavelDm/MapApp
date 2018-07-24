package com.vpaveldm.mapapp.viewModel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.vpaveldm.mapapp.model.Marker

class MapViewModel : ViewModel(), IMarkerManager {

    override fun addedMarker(marker: Marker): Boolean {
        if (markers.size == 2) return false
        markers.add(marker)
        markerLiveData.value = marker
        return true
    }

    val markers: ArrayList<Marker> = arrayListOf()
    val markerLiveData: MutableLiveData<Marker> = MutableLiveData()
}