package com.vpaveldm.mapapp.viewModel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.vpaveldm.mapapp.Application
import com.vpaveldm.mapapp.TAG
import com.vpaveldm.mapapp.model.Marker
import com.vpaveldm.mapapp.model.server.Result
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CoordinateViewModel : ViewModel() {

    fun sendRequestForElevates(first: Marker, second: Marker, samples: Int) {
        val requestStr = "${first.latitude},${first.longitude}|${second.latitude}, ${second.longitude}"
        //TODO разбить запросы
        val request = Application.getMapService().getCoordinates(requestStr, samples)
        request.enqueue(object : Callback<Result> {
            override fun onResponse(call: Call<Result>, response: Response<Result>) {
                Log.i(TAG, response.body()?.results?.size.toString())
                response.body()?.let { coordinateResult.value = it }
            }

            override fun onFailure(call: Call<Result>, t: Throwable) {
                errorLiveData.value = t.message
            }
        })
    }

    val coordinateResult: MutableLiveData<Result> = MutableLiveData()
    val errorLiveData = MutableLiveData<String>()
}