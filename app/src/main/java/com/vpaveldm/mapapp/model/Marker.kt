package com.vpaveldm.mapapp.model

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.vpaveldm.mapapp.model.server.Location
import java.io.Serializable

data class Marker(val latitude: Double, val longitude: Double) : Serializable {
    constructor(position: LatLng) : this(position.latitude, position.longitude)
    constructor(marker: Marker) : this(marker.position.latitude, marker.position.longitude)
    constructor(location: Location) : this(location.lat!!, location.lng!!)
}