package com.vpaveldm.mapapp.model

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

data class Marker(val latitude: Double, val longitude: Double) {
    constructor(position: LatLng) : this(position.latitude, position.longitude)
    constructor(marker: Marker) : this(marker.position.latitude, marker.position.longitude)
}