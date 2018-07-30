package com.vpaveldm.mapapp.view.map

import android.Manifest
import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.vpaveldm.mapapp.R
import com.vpaveldm.mapapp.model.Marker
import com.vpaveldm.mapapp.view.graphic.GraphicActivity
import com.vpaveldm.mapapp.viewModel.MapViewModel
import kotlinx.android.synthetic.main.activity_map.*
import com.google.android.gms.maps.model.Marker as GoogleMarker

class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener, IMapTypeManager {
    override fun mapSelected(type: MapType) {
        when (type) {
            MapType.TERRAIN -> map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            MapType.SATELLITE -> map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            MapType.MAP -> map.mapType = GoogleMap.MAP_TYPE_NORMAL
        }
    }

    override fun onMapClick(p0: LatLng?) {
        changeBottomView(BottomViewMode.DEFAULT)
    }

    override fun onMarkerClick(marker: GoogleMarker?): Boolean {
        if (marker == null)
            return false
        if (navigation.menu.size() == 1) {
            changeBottomView(BottomViewMode.EDIT)
            viewModel.lastSelectedMarker = marker
        }
        return false
    }

    override fun onMapLongClick(position: LatLng?) {
        position?.let {
            if (!viewModel.addedMarker(Marker(position)))
                Toast.makeText(this, getString(R.string.error_add_marker_limit), LENGTH_LONG).show()
            changeBottomView(BottomViewMode.DEFAULT)
        } ?: Toast.makeText(this, getString(R.string.error_add_marker), LENGTH_LONG).show()
    }

    override fun onMapReady(map: GoogleMap?) {
        map?.let {
            this.map = it
            this.map.setOnMapLongClickListener(this)
            this.map.setOnMarkerClickListener(this)
            this.map.setOnMapClickListener(this)
            this.map.mapType = GoogleMap.MAP_TYPE_HYBRID
            repaint()
        } ?: Toast.makeText(this, getString(R.string.error_init_map), LENGTH_LONG).show()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        Log.i(com.vpaveldm.mapapp.TAG, "onCreate")

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        myLocationFAB.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    val message = "Требуется доступ к местоположению"
                    Snackbar.make(container, message, Snackbar.LENGTH_LONG)
                            .setAction("GRANT") {
                                moveCamera()
                            }
                            .show()
                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_ACCESS_KEY)
                }
            } else {
                moveCamera()
            }
        }
        mapTypeFAB.setOnClickListener {
            val dialog = MapTypeDialog()
            dialog.show(supportFragmentManager, null)
        }

        viewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }

    override fun onResume() {
        super.onResume()
        Log.i(com.vpaveldm.mapapp.TAG, "onResume")
        viewModel.markerLiveData.observe(this, Observer {
            changeBottomView(BottomViewMode.DEFAULT)
            repaint()
        })
        viewModel.errorLiveData.observe(this, Observer { Toast.makeText(this, it, LENGTH_LONG).show() })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_ACCESS_KEY -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED))
                    moveCamera()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    lateinit var viewModel: MapViewModel
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_add -> {
                val dialog = WorkMarkerDialog()
                dialog.show(supportFragmentManager, WorkMarkerMode.ADD.name)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_remove -> {
                viewModel.markers.remove(viewModel.convertGoogleMarker())
                changeBottomView(BottomViewMode.ADD)
                repaint()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_edit -> {
                val dialog = WorkMarkerDialog()
                dialog.show(supportFragmentManager, WorkMarkerMode.EDIT.name)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_graphic -> {
                val intent = GraphicActivity.newIntent(this, viewModel.markers[0], viewModel.markers[1])
                startActivity(intent)
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
        Log.i(com.vpaveldm.mapapp.TAG, "repaint")
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

    @SuppressLint("MissingPermission")
    private fun moveCamera() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            run {
                if (location == null) {
                    Toast.makeText(this, "Нет доступа к местоположению", LENGTH_LONG).show()
                    return@run
                }
                val position = LatLng(location.latitude, location.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15f))
                map.addMarker(MarkerOptions()
                        .position(position)
                        .icon(bitmapDescriptorFromVector(this, R.drawable.ic_pin)))
            }
        }
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
        vectorDrawable.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

}

private const val LOCATION_ACCESS_KEY = 1