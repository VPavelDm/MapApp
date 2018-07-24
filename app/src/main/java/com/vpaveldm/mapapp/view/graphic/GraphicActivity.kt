package com.vpaveldm.mapapp.view.graphic

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.vpaveldm.mapapp.R
import com.vpaveldm.mapapp.model.Marker
import com.vpaveldm.mapapp.viewModel.CoordinateViewModel

class GraphicActivity : AppCompatActivity() {

    private lateinit var viewModel: CoordinateViewModel

    private lateinit var chart: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graphic)
        chart = findViewById(R.id.chart_view)
        viewModel = ViewModelProviders.of(this).get(CoordinateViewModel::class.java)
        val arguments = intent.extras ?: return
        viewModel.sendRequestForElevates(
                arguments.getSerializable("firstMarker") as Marker,
                arguments.getSerializable("secondMarker") as Marker,
                arguments.getInt("samples")
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.coordinateResult.observe(this, Observer {
            val entities = arrayListOf<Entry>()
            if (it?.results == null || it.results!!.isEmpty())
                return@Observer

            for ((index, res) in it.results!!.withIndex()) {
                res.elevation?.let {
                    entities += Entry(index.toFloat(), it.toFloat())
                }
            }
            val set = LineDataSet(entities, "Перепады высот")
            set.color = Color.RED
            val lineData = LineData(set)
            chart.data = lineData
            chart.invalidate()
        })
        viewModel.errorLiveData.observe(this, Observer {
            it?.let { Toast.makeText(this, it, Toast.LENGTH_LONG).show() }
        })
    }

    companion object {
        fun newIntent(context: Context, first: Marker, second: Marker): Intent {
            val intent = Intent(context, GraphicActivity::class.java)
            intent.putExtra("firstMarker", first)
            intent.putExtra("secondMarker", second)
            val samples = calculateDistance(first, second)
            intent.putExtra("samples", samples)
            return intent
        }
    }
}

private fun calculateDistance(first: Marker, second: Marker): Double {
    val R = 6378137; // Earth’s mean radius in meter
    val dLat = rad(second.latitude - first.latitude)
    val dLong = rad(second.longitude - first.longitude)
    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(rad(first.latitude)) * Math.cos(rad(second.latitude)) *
            Math.sin(dLong / 2) * Math.sin(dLong / 2);
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    val d = R * c;
    return d // returns the distance in meter
}

private fun rad(x: Double) = x * Math.PI / 180