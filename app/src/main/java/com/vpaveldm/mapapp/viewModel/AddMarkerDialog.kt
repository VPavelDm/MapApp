package com.vpaveldm.mapapp.viewModel

import android.support.v4.app.DialogFragment
import com.vpaveldm.mapapp.model.Marker

interface IMarkerManager {
    fun addedMarker(marker: Marker): Boolean
}

class AddMarkerDialog : DialogFragment() {

}