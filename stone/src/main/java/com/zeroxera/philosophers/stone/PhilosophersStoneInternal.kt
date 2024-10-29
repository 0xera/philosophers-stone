package com.zeroxera.philosophers.stone

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import java.util.concurrent.Semaphore
import kotlin.LazyThreadSafetyMode.NONE
import kotlin.properties.Delegates

internal object PhilosophersStoneInternal {
    lateinit var app: Application

    var isEnabled = false
    var isLifeTimeCalculationEnabled = false
    var isLoggingEnabled: Boolean = false

    var isForeground by Delegates.observable(false) { _, _, newValue -> log { "Manager: isForeground = $newValue" } }
        private set

    val mainHandler by lazy(NONE) { Handler(Looper.getMainLooper()) }

    val receiverLock = Semaphore(1).apply { acquire() }
    val serviceLock = Semaphore(1).apply { acquire() }

    private val id = SystemClock.elapsedRealtime().toString(16)

    var worker: Worker? = null

    val callbacks by lazy {
        object : ActivityLifecycleCallbacks {
            private var resumedActivitiesCount = 0

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
            override fun onActivityStarted(activity: Activity) = Unit
            override fun onActivityResumed(activity: Activity) {
                resumedActivitiesCount++
                isForeground = resumedActivitiesCount > 0
                if (isForeground) {
                    serviceLock.release()
                    serviceLock.tryAcquire()
                    receiverLock.release()
                    receiverLock.tryAcquire()
                }
            }

            override fun onActivityPaused(activity: Activity) {
                resumedActivitiesCount--
                isForeground = resumedActivitiesCount > 0
                mainHandler.postDelayed({
                    isForeground = resumedActivitiesCount > 0
                    if (!isEnabled) {
                        log { "Manager: Stop" }
                        app.unregisterActivityLifecycleCallbacks(this)
                        app.stopService(Intent(app, PhilosophersStoneService::class.java))
                    }
                }, 1000)
            }

            override fun onActivityStopped(activity: Activity) {
                if (!isForeground && isEnabled) {
                    start()
                }
            }

            private fun start() {
                if (shouldUseService()) {
                    log { "Manager: App start service" }
                    app.startService(Intent(app, PhilosophersStoneService::class.java))
                } else {
                    log { "Manager: App send broadcast" }
                    app.sendBroadcast(Intent(app, PhilosophersStoneReceiver::class.java))
                    app.startService(Intent(app, PhilosophersStickyService::class.java))
                }
                startLivingTimeCalculation()
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
            override fun onActivityDestroyed(activity: Activity) = Unit
        }
    }

    fun log(msg: () -> String) {
        if (isLoggingEnabled) Log.d("PhilosophersStone", msg())
    }

    fun startLivingTimeCalculation() {
        if (isLifeTimeCalculationEnabled) {
            if (id == app.sharedPreferences.getString("app_id", "")) {
                return
            }
            val time = SystemClock.elapsedRealtime()
            app.sharedPreferences.edit()
                .putString("app_id", id)
                .putLong("start_time", time)
                .putLong("end_time", time)
                .apply()
        }
    }

    fun saveLastLifeTime() {
        if (isLifeTimeCalculationEnabled) {
            val time = SystemClock.elapsedRealtime()
            app.sharedPreferences.edit()
                .putLong("end_time", time)
                .apply()
        }
    }

    internal fun retrieveLastLifeDuration(): Long {
        val startTime = app.sharedPreferences.getLong("start_time", 0)
        val endTime = app.sharedPreferences.getLong("end_time", 0)
        return endTime - startTime
    }

    enum class Worker {
        BroadcastReceiver,
        Service
    }
}