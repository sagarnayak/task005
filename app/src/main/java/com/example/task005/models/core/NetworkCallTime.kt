package com.example.task005.models.core

data class NetworkCallTime(
    var startTime: Long = 0,
    var endTime: Long = 0,
    var timeTaken: Long = 0,
    var ema: Double = 0.0,
    var sma: Double = 0.0
)