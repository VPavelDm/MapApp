package com.vpaveldm.mapapp.viewModel

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import com.vpaveldm.mapapp.R
import com.vpaveldm.mapapp.model.Marker
import com.vpaveldm.mapapp.view.map.MapActivity

interface IMarkerManager {
    fun addedMarker(marker: Marker): Boolean
    fun changeMarker(marker: Marker)
    fun error(message: String)
}

enum class WorkMarkerMode {
    ADD, EDIT
}

class WorkMarkerDialog : DialogFragment() {

    private var listener: IMarkerManager? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (context is MapActivity)
            listener = (context as MapActivity).viewModel
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.i(com.vpaveldm.mapapp.TAG, "dialog: onCreateDialog")
        val view = LayoutInflater.from(context!!).inflate(R.layout.add_marker_view, null)
        val latitudeET = view.findViewById<EditText>(R.id.latitudeET)
        val longitudeET = view.findViewById<EditText>(R.id.longitudeET)
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(context!!.getString(R.string.label_add_marker))
                .setView(view)
                .setPositiveButton(context!!.getString(R.string.label_add)) { _, _ ->
                    run {
                        try {
                            val latitude = latitudeET.text.toString().toDouble()
                            val longitude = longitudeET.text.toString().toDouble()
                            tag?.let {
                                when (WorkMarkerMode.valueOf(it)) {
                                    WorkMarkerMode.ADD -> listener?.addedMarker(Marker(latitude, longitude))
                                    WorkMarkerMode.EDIT -> listener?.changeMarker(Marker(latitude, longitude))
                                }
                            }
                        } catch (e: NumberFormatException) {
                            listener?.error(context!!.getString(R.string.error_input_data))
                        }
                    }
                }
        return builder.create()
    }
}