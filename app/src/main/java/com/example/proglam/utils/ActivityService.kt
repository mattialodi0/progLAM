package com.example.proglam.utils

import androidx.core.app.NotificationCompat

interface ActivityService {
    enum class Actions {
        START, STOP
    }

    fun start()
    fun stop()
    fun abort(e: Throwable)
    fun getNotificationBuilder(): NotificationCompat.Builder
}