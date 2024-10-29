package com.zeroxera.philosophers.stone

import android.app.Application

object PhilosophersStone {

    fun init(
        app: Application,
        enabled: Boolean,
        logging: Boolean = false,
        calculateLifeTime: Boolean = false
    ) {
        PhilosophersStoneInternal.app = app
        PhilosophersStoneInternal.isLoggingEnabled = logging
        PhilosophersStoneInternal.isLifeTimeCalculationEnabled = calculateLifeTime
        PhilosophersStoneInternal.isEnabled = enabled
        if (enabled) {
            app.registerActivityLifecycleCallbacks(PhilosophersStoneInternal.callbacks)
        }
    }

    fun retrieveLastLifeDuration() = PhilosophersStoneInternal.retrieveLastLifeDuration()

}