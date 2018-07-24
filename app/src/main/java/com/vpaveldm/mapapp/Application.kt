package com.vpaveldm.mapapp

import android.app.Application
import com.vpaveldm.mapapp.model.server.MapService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        val retrofit = Retrofit.Builder()
                .baseUrl(ELEVATION_MAP_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        mapService = retrofit.create(MapService::class.java)
    }

    companion object {
        private lateinit var mapService: MapService
        fun getMapService(): MapService = mapService
    }
}

private const val ELEVATION_MAP_BASE_URL = "https://maps.googleapis.com/"
const val TAG = "com.vpaveldm.tag"