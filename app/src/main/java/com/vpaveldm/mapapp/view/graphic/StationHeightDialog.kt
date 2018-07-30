package com.vpaveldm.mapapp.view.graphic

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import com.vpaveldm.mapapp.R
import kotlinx.android.synthetic.main.change_station_height.view.*

class StationHeightDialog : DialogFragment() {

    private lateinit var listener: IStationHeightManager

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = (context as GraphicActivity).viewModel
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context!!).inflate(R.layout.change_station_height, null)
        val builder = AlertDialog.Builder(context!!)
        builder.setView(view)
                .setTitle(getString(R.string.station_height))
                .setPositiveButton(getString(R.string.change)) { _, _ ->
                    run {
                        var firstHeight: Int
                        var secondHeight: Int
                        try {
                            firstHeight = view.firstStationET.text.toString().toInt()
                        } catch (e: NumberFormatException) {
                            firstHeight = 0
                        }
                        try {
                            secondHeight = view.secondStationET.text.toString().toInt()
                        } catch (e: NumberFormatException) {
                            secondHeight = 0
                        }
                        listener.heightChanged(firstHeight, secondHeight)
                    }
                }
        return builder.create()
    }

}

interface IStationHeightManager {
    fun heightChanged(firstHeight: Int, secondHeight: Int)
    fun error(message: String)
}