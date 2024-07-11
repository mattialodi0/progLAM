package com.example.proglam.utils

object Strings {
    fun formattedTimer(seconds: Long): String {
        if (seconds <= 0) return "00:00:00"
        else {
            var h = 0L
            var m = 0L
            var s = seconds

            while (s >= 3600) {
                h++
                s -= 3600
            }
            while (s >= 60) {
                m++
                s -= 60
            }

            var hs: String
            var ms: String
            var ss: String

            if (h < 10) hs = "0$h"
            else hs = "$h"
            if (m < 10) ms = "0$m"
            else ms = "$m"
            if (s < 10) ss = "0$s"
            else ss = "$s"

            return "$hs:$ms:$ss"
        }
    }
}