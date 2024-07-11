package com.example.proglam.utils

sealed class TimerEvent {
    data object START : TimerEvent()
    data object END : TimerEvent()
    data object ABORT : TimerEvent()
}