package com.example.task005.utils

import com.example.task005.contracts.NetworkCallTimeMaster
import com.example.task005.enums.NetworkSpeed
import com.example.task005.models.core.NetworkCallTime
import kotlin.math.min

class NetworkCallTimeMaster : NetworkCallTimeMaster() {

    override fun gotNetworkCallTime(newNetworkCallTime: NetworkCallTime) {
        newNetworkCallTime.timeTaken = newNetworkCallTime.endTime - newNetworkCallTime.startTime
        networkCallTimes.add(newNetworkCallTime)

        if (networkCallTimes.size < NETWORK_TIME_CALCULATION_PERIOD_MIN)
            return

        val averagePeriod = min(NETWORK_TIME_CALCULATION_PERIOD, networkCallTimes.size)
        val smoothingFactor = NETWORK_TIME_CALCULATION_EMA_SMOOTHING

        networkCallTimes.forEachIndexed { index, networkCallTime ->
            if (index >= (averagePeriod - 1)) {
                var calculatedSMA = 0.0
                networkCallTimes.subList(
                    (index + 1) - averagePeriod,
                    index
                ).forEach {
                    calculatedSMA += it.timeTaken
                }

                networkCallTime.sma = calculatedSMA / averagePeriod

                if (index == (averagePeriod - 1)) {
                    networkCallTime.ema = calculatedSMA
                } else {
                    networkCallTime.ema =
                        (
                                (
                                        networkCallTime.timeTaken -
                                                networkCallTimes[index - 1].ema
                                        ) *
                                        smoothingFactor
                                ) +
                                networkCallTimes[index - 1].ema
                }
            }
        }

        networkSpeed =
            if (networkCallTimes[networkCallTimes.size - 1].ema > NETWORK_SPEED_THRESHOLD_MILLS)
                NetworkSpeed.SLOW
            else
                NetworkSpeed.FAST
    }
}