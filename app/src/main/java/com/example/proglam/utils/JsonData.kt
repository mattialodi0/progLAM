package com.example.proglam.utils

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName


class JsonData(
    p: ArrayList<LatLng>,
    s: Int
) {
    @SerializedName("positions")
    val positions: ArrayList<LatLng> = p
    @SerializedName("steps")
    val steps: Int = s
}
