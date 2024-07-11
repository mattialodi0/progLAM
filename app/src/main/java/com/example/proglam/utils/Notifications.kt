package com.example.proglam.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.content.ContextCompat.getSystemService

class Notifications {
    companion object {
        const val CHANNEL_ID = "jobScheduling_channel"
        const val ONGOING_CHANNEL_ID = "ongoing_channel"
    }
}