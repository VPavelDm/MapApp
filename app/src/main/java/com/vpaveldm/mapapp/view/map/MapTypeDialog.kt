package com.vpaveldm.mapapp.view.map

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import com.vpaveldm.mapapp.R
import kotlinx.android.synthetic.main.change_map_type_view.view.*

class MapTypeDialog : DialogFragment() {

    private lateinit var listener: IMapTypeManager

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as IMapTypeManager
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context!!).inflate(R.layout.change_map_type_view, null)
        val builder = AlertDialog.Builder(context!!)
        view.map.setOnClickListener {
            listener.mapSelected(MapType.MAP)
            dismiss()
        }
        view.terrain.setOnClickListener {
            listener.mapSelected(MapType.TERRAIN)
            dismiss()
        }
        view.satellite.setOnClickListener {
            listener.mapSelected(MapType.SATELLITE)
            dismiss()
        }
        builder.setTitle(getString(R.string.map_type))
                .setView(view)
        return builder.create()
    }
}

enum class MapType {
    TERRAIN, SATELLITE, MAP
}

interface IMapTypeManager {
    fun mapSelected(type: MapType)
}