package com.example.proglam.utils

import com.google.gson.annotations.SerializedName

class JsonData(
    p: ArrayList<Pair<Double, Double>>,
    s: Int
) {
    @SerializedName("positions")
    val positions: ArrayList<Pair<Double, Double>> = p
    @SerializedName("steps")
    val steps: Int = s
}
