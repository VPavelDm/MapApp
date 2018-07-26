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
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.vpaveldm.mapapp.R
import com.vpaveldm.mapapp.model.Marker
import com.vpaveldm.mapapp.viewModel.CoordinateViewModel
import com.vpaveldm.mapapp.viewModel.Factory

class GraphicActivity : AppCompatActivity() {

    private lateinit var viewModel: CoordinateViewModel

    private lateinit var chart: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graphic)
        chart = findViewById(R.id.chart_view)
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.setAvoidFirstLastClipping(true)
        val arguments = intent.extras ?: return
        val first = arguments.getSerializable("firstMarker") as Marker
        val second = arguments.getSerializable("secondMarker") as Marker
        viewModel = ViewModelProviders.of(this, Factory(first, second)).get(CoordinateViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        viewModel.coordinateResult.observe(this, Observer {
            val entities = arrayListOf<Entry>()
            if (it == null || it.isEmpty())
                return@Observer
            for (res in it) {
                val d = res.distance ?: continue
                res.elevation?.let {
                    entities += Entry(d.toFloat(), it.toFloat())
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
            return intent
        }
    }
}