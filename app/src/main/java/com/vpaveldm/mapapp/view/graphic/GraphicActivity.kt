package com.vpaveldm.mapapp.view.graphic

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.vpaveldm.mapapp.R
import com.vpaveldm.mapapp.model.Marker
import com.vpaveldm.mapapp.viewModel.CoordinateViewModel
import com.vpaveldm.mapapp.viewModel.Factory
import kotlinx.android.synthetic.main.activity_graphic.*


class GraphicActivity : AppCompatActivity() {

    private lateinit var viewModel: CoordinateViewModel

    private lateinit var chart: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graphic)
        chart = findViewById(R.id.chart_view)
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.setAvoidFirstLastClipping(true)
        chart.setNoDataText("")
        val arguments = intent.extras ?: return
        val first = arguments.getSerializable("firstMarker") as Marker
        val second = arguments.getSerializable("secondMarker") as Marker
        viewModel = ViewModelProviders.of(this, Factory(first, second)).get(CoordinateViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        progressBar.visibility = View.VISIBLE
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

            val dataSets = ArrayList<ILineDataSet>()
            set.color = Color.GREEN
            set.setDrawFilled(true)
            set.fillColor = Color.GREEN

            dataSets.add(set)

            val entities2 = arrayListOf<Entry>()

            entities2.add(Entry(0F, it[0].elevation!!.toFloat() + 30))
            entities2.add(Entry(it.last().distance!!.toFloat(), it.last().elevation!!.toFloat() + 30))


            val set2 = LineDataSet(entities2, "Волна")
            set2.color = Color.RED
            set2.lineWidth = 3f

            dataSets.add(set2)

            val lineData = LineData(dataSets)

            chart.data = lineData
            progressBar.visibility = View.INVISIBLE

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