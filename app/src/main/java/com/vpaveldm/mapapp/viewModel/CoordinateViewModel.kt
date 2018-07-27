package com.vpaveldm.mapapp.viewModel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.util.Log
import com.vpaveldm.mapapp.Application
import com.vpaveldm.mapapp.TAG
import com.vpaveldm.mapapp.model.Marker
import com.vpaveldm.mapapp.model.server.Coordinate
import com.vpaveldm.mapapp.model.server.Result
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.ConnectException

class CoordinateViewModel(private val first: Marker, private val second: Marker) : ViewModel() {

    init {
        sendRequestForElevates()
    }

    private fun sendRequestForElevates() {
        //TODO progress bar
        val d = calculateDistance(first, second)
        val countPer512 = (d / (DISTANCE_BETWEEN_POINT * 512)).toInt()
        val countNotPer512 = ((d / (DISTANCE_BETWEEN_POINT * 512)) / DISTANCE_BETWEEN_POINT).toInt() + 1
        if (countPer512 == 0) {
            val count = (d / DISTANCE_BETWEEN_POINT).toInt() + 3
            val requestStr = "${first.latitude},${first.longitude}|${second.latitude}, ${second.longitude}"
            val request = Application.getMapService().getCoordinates(requestStr, count)
            request.enqueue(object : Callback<Result> {
                override fun onResponse(call: Call<Result>, response: Response<Result>) {
                    Log.i(TAG, response.body()?.results?.size.toString())
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result?.results == null) {
                            errorLiveData.value = "Ошибка запроса... Повторите позже"
                            return
                        }
                        for ((index, i) in result.results!!.withIndex()) {
                            i.distance = index * 7
                        }
                        coordinateResult.value = result.results
                    } else if (response.body()?.status == "OVER_QUERY_LIMIT") {
                        errorLiveData.value = "На текущий момент количество запросов исчерпано"
                    }
                }

                override fun onFailure(call: Call<Result>, t: Throwable) {
                    errorLiveData.value = REQUEST_ERROR
                }
            })
        } else {
            val allCoordinates = arrayListOf<Coordinate>()
            Thread(Runnable {
                try {
                    val countInterval = d / (DISTANCE_BETWEEN_POINT * 512)
                    val intervals = getCoordinates(first, second, countInterval.toInt() + 3)
                    for (i in 1 until intervals.size - 1) {
                        val f = intervals[i - 1].location
                        val s = intervals[i].location
                        allCoordinates.addAll(getCoordinates(Marker(f), Marker(s), 512))
                    }
                    val f = intervals[intervals.size - 2].location
                    val s = intervals[intervals.size - 1].location
                    allCoordinates.addAll(getCoordinates(Marker(f), Marker(s), countNotPer512))
                    if (allCoordinates.size == 0) {
                        coordinateResult.postValue(arrayListOf())
                        return@Runnable
                    }
                    val answer = arrayListOf<Coordinate>()
                    allCoordinates[0].distance = 0
                    val first = allCoordinates[0].elevation!!
                    val second = allCoordinates[1].elevation!!
                    var signMinus = second - first < 0
                    var curHeight = allCoordinates[0]
                    answer.add(curHeight)
                    for ((index, cor) in allCoordinates.withIndex()) {
                        if ((signMinus && cor.elevation!! > curHeight.elevation!!) ||
                                (!signMinus && cor.elevation!! < curHeight.elevation!!)) {
                            curHeight.distance = index * 7
                            answer.add(curHeight)
                            signMinus = !signMinus
                        }
                        curHeight = cor
                    }

                    coordinateResult.postValue(answer)
                } catch (e: ConnectException) {
                    errorLiveData.postValue(e.message)
                }
            }).start()
        }
    }

    val coordinateResult: MutableLiveData<List<Coordinate>> = MutableLiveData()
    val errorLiveData = MutableLiveData<String>()
}

class Factory(val first: Marker, val second: Marker) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T = CoordinateViewModel(first, second) as T
}

private fun getCoordinates(first: Marker, second: Marker, samples: Int): List<Coordinate> {
    val requestStr = "${first.latitude},${first.longitude}|${second.latitude}, ${second.longitude}"
    val request = Application.getMapService().getCoordinates(requestStr, samples)
    val response = request.execute()
    if (!response.isSuccessful) {
        throw ConnectException(REQUEST_ERROR)
    }
    val result = response.body() ?: throw ConnectException(REQUEST_ERROR)
    return result.results ?: throw ConnectException(REQUEST_ERROR)
}

private fun calculateDistance(first: Marker, second: Marker): Double {
    val radius = 6378137; // Earth’s mean radius in meter
    val dLat = rad(second.latitude - first.latitude)
    val dLong = rad(second.longitude - first.longitude)
    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(rad(first.latitude)) * Math.cos(rad(second.latitude)) *
            Math.sin(dLong / 2) * Math.sin(dLong / 2);
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    val d = radius * c;
    return d // returns the distance in meter
}

private fun rad(x: Double) = x * Math.PI / 180

private const val DISTANCE_BETWEEN_POINT = 7
private const val REQUEST_ERROR = "Ошибка получения высот... Проверьте соединение с интернетом"