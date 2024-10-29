package com.zeroxera.philosophers.stone

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.zeroxera.philosophers.stone.PhilosophersStoneInternal.Worker

class PhilosophersStickyService : Service() {
    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!PhilosophersStoneInternal.isEnabled) stopSelf()
        if (intent == null) {
            PhilosophersStoneInternal.log { "Sticky Service: sendBroadcast" }
            sendBroadcast(Intent(this, PhilosophersStoneReceiver::class.java))
        }
        return START_STICKY
    }
}