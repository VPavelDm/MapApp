package com.vpaveldm.mapapp.model

import com.google.android.gms.maps.model.LatLng

data class Marker(val latitude: Double, val longitude: Double) {
    constructor(position: LatLng) : this(position.latitude, position.longitude)
}