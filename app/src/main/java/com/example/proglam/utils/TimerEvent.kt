package com.example.proglam.utils

sealed class TimerEvent {
    object START : TimerEvent()
    object END : TimerEvent()
    object ABORT : TimerEvent()
}