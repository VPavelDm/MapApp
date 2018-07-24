package com.vpaveldm.mapapp.model.server

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MapService {
    @GET("maps/api/elevation/json")
    fun getCoordinates(@Query("path") path: String, @Query("samples") samples: Int) : Call<Result>
}