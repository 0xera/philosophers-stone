package com.zeroxera.philosophers.stone.app

import android.app.Application
import android.os.SystemClock
import android.util.Log
import com.zeroxera.philosophers.stone.PhilosophersStone

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Application: onCreate() called")

        PhilosophersStone.init(
            app = this,
            enabled = true,
            logging = true,
            calculateLifeTime = true
        )

        super.onCreate()
    }

    companion object {
        val ID = SystemClock.elapsedRealtime().toString(16)
    }
}