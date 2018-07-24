package com.vpaveldm.mapapp.model.server

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Result {

    @SerializedName("results")
    @Expose
    var results: List<Coordinate>? = null

}

class Coordinate {

    @SerializedName("elevation")
    @Expose
    var elevation: Double? = null

    @SerializedName("location")
    @Expose
    var location: Location? = null

    @SerializedName("resolution")
    @Expose
    var resolution: Double? = null

}

class Location {

    @SerializedName("lat")
    @Expose
    var lat: Double? = null

    @SerializedName("lng")
    @Expose
    var lng: Double? = null

}