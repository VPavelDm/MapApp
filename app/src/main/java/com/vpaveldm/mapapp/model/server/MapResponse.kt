package com.vpaveldm.mapapp.model.server

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Result {

    @SerializedName("results")
    @Expose
    var results: List<Coordinate>? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

}

class Coordinate {

    @SerializedName("elevation")
    @Expose
    var elevation: Double? = null

    @SerializedName("location")
    @Expose
    lateinit var location: Location

    @SerializedName("resolution")
    @Expose
    var resolution: Double? = null

    var distance: Int? = null

}

class Location {

    @SerializedName("lat")
    @Expose
    var lat: Double? = null

    @SerializedName("lng")
    @Expose
    var lng: Double? = null

}