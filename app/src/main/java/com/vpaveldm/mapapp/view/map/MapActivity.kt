package com.vpaveldm.mapapp.view.map

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.vpaveldm.mapapp.R
import com.vpaveldm.mapapp.model.Marker
import com.vpaveldm.mapapp.viewModel.AddMarkerDialog
import com.vpaveldm.mapapp.viewModel.MapViewModel
import kotlinx.android.synthetic.main.activity_map.*

class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    override fun onMapLongClick(position: LatLng?) {
        position?.let { addMarker(it) }
                ?: Toast.makeText(this, getString(R.string.error_add_marker), LENGTH_LONG).show()
    }

    override fun onMapReady(map: GoogleMap?) {
        map?.let {
            this.map = it
            this.map.setOnMapLongClickListener(this)
            repaint()
        } ?: Toast.makeText(this, getString(R.string.error_init_map), LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        viewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onResume() {
        super.onResume()
        viewModel.markerLiveData.observe(this, Observer { repaint() })
    }

    private lateinit var map: GoogleMap
    private lateinit var viewModel: MapViewModel
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_add -> {
                val dialog = AddMarkerDialog()
                dialog.show(supportFragmentManager, null)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun changeBottomView(mode: BottomViewMode) {
        navigation.menu.clear()
        when (mode) {
            BottomViewMode.ADD -> navigation.inflateMenu(R.menu.navigation_add)
            BottomViewMode.EDIT -> navigation.inflateMenu(R.menu.navigation_edit)
            BottomViewMode.GRAPHIC -> navigation.inflateMenu(R.menu.navigation_graphic)
            BottomViewMode.DEFAULT -> {
                if (viewModel.markers.size == 2) {
                    changeBottomView(BottomViewMode.GRAPHIC)
                } else {
                    changeBottomView(BottomViewMode.ADD)
                }
            }
        }
    }

    private fun repaint() {
        if (!this::map.isInitialized)
            return
        map.clear()
        for ((index, marker) in viewModel.markers.withIndex()) {
            map.addMarker(
                    MarkerOptions()
                            .title("${if (index == 0) "Первый" else "Второй"} маркер")
                            .position(LatLng(marker.latitude, marker.longitude))
            )
        }
        map.addPolyline(
                PolylineOptions()
                        .width(7f)
                        .color(Color.RED)
                        .addAll(viewModel.markers.map { LatLng(it.latitude, it.longitude) })
        )
    }

    private enum class BottomViewMode {
        GRAPHIC, EDIT, ADD, DEFAULT
    }

    private fun addMarker(position: LatLng) {
        if (!viewModel.addedMarker(Marker(position)))
            Toast.makeText(this, getString(R.string.error_add_marker_limit), LENGTH_LONG).show()
        changeBottomView(BottomViewMode.DEFAULT)
    }
}
