package com.example.proglam.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.util.TypedValue
import java.security.AccessController.getContext


object System {

    fun isNightModeOn(context: Context): Boolean {
        val nightModeFlags: Int = context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> return true
        }
        return false
    }

    fun floatToDP(float: Float, resources: Resources): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, float, resources.displayMetrics)
    }

    fun floatToSP(float: Float, resources: Resources): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, float, resources.displayMetrics)
    }
}