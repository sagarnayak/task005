package com.example.task005.contracts

import com.example.task005.enums.NetworkSpeed
import com.example.task005.models.core.NetworkCallTime

abstract class NetworkCallTimeMaster {
    companion object {
        const val NETWORK_TIME_CALCULATION_PERIOD = 10
        const val NETWORK_TIME_CALCULATION_PERIOD_MIN = 4
        const val NETWORK_TIME_CALCULATION_EMA_SMOOTHING = 0.181818
        const val NETWORK_SPEED_THRESHOLD_MILLS = 1000L
    }

    protected val networkCallTimes: ArrayList<NetworkCallTime> = ArrayList()
    protected var networkSpeed = NetworkSpeed.CAN_NOT_DETERMINE

    fun getCurrentNetworkSpeed() = networkSpeed

    abstract fun gotNetworkCallTime(newNetworkCallTime: NetworkCallTime)
}