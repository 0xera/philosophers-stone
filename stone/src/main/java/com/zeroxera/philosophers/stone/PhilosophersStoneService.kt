package com.zeroxera.philosophers.stone

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.zeroxera.philosophers.stone.PhilosophersStoneInternal.Worker

class PhilosophersStoneService : Service() {
    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!PhilosophersStoneInternal.isEnabled) stopSelf()
        return START_STICKY
    }

    override fun onDestroy() {
        if (!PhilosophersStoneInternal.isEnabled) {
            super.onDestroy()
            return
        }

        if (PhilosophersStoneInternal.worker == Worker.BroadcastReceiver) {
            PhilosophersStoneInternal.log { "Service: Broadcast Receiver already works" }
            super.onDestroy()
            return
        }
        if (shouldUseService()) {
            PhilosophersStoneInternal.log { "Service: onDestroy" }
            if (!PhilosophersStoneInternal.isForeground) {
                PhilosophersStoneInternal.worker = Worker.Service
                PhilosophersStoneInternal.log { "Service: wait" }

                for (i in 0 until 1980) { // 200s for anr
                    if (PhilosophersStoneInternal.isForeground) break
                    if (PhilosophersStoneInternal.mainHandler.looper.queue.isIdle) {
                        Thread.sleep(100)
                    } else {
                        PhilosophersStoneInternal.mainHandler.looper.queue.addIdleHandler {
                            if (!PhilosophersStoneInternal.isForeground) {
                                PhilosophersStoneInternal.log { "Service: sendBroadcast from IdleHandler" }
                                sendBroadcast(Intent(this, PhilosophersStoneReceiver::class.java))
                                PhilosophersStoneInternal.saveLastLifeTime()
                            }
                            false // remove after invoke
                        }
                        PhilosophersStoneInternal.saveLastLifeTime()
                        super.onDestroy()
                        return
                    }
                }
            }
        }
        if (!PhilosophersStoneInternal.isForeground) {
            PhilosophersStoneInternal.log { "Service: sendBroadcast" }
            sendBroadcast(Intent(this, PhilosophersStoneReceiver::class.java))
            PhilosophersStoneInternal.saveLastLifeTime()
        } else {
            PhilosophersStoneInternal.saveLastLifeTime()
        }
        super.onDestroy()
    }
}