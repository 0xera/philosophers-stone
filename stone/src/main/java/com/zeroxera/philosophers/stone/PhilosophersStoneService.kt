package com.zeroxera.philosophers.stone

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.zeroxera.philosophers.stone.PhilosophersStoneInternal.Worker
import com.zeroxera.philosophers.stone.PhilosophersStoneInternal.isForeground
import com.zeroxera.philosophers.stone.PhilosophersStoneInternal.log
import com.zeroxera.philosophers.stone.PhilosophersStoneInternal.mainHandler
import com.zeroxera.philosophers.stone.PhilosophersStoneInternal.saveLastLifeTime
import com.zeroxera.philosophers.stone.PhilosophersStoneInternal.worker

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

        if (worker == Worker.BroadcastReceiver) {
            log { "Service: Broadcast Receiver already works" }
            super.onDestroy()
            return
        }
        if (shouldUseService()) {
            log { "Service: onDestroy" }
            if (!isForeground) {
                worker = Worker.Service
                log { "Service: wait" }

                for (i in 0 until 1970) { // before 200s for anr and unix time
                    if (isForeground) break
                    if (mainHandler.looper.queue.isIdle) {
                        Thread.sleep(100)
                    } else {
                        mainHandler.looper.queue.addIdleHandler {
                            if (!isForeground) {
                                log { "Service: sendBroadcast from IdleHandler" }
                                sendBroadcast(Intent(this, PhilosophersStoneReceiver::class.java))
                                saveLastLifeTime()
                            }
                            false // remove after invoke
                        }
                        saveLastLifeTime()
                        super.onDestroy()
                        return
                    }
                }
            }
        }
        if (!isForeground) {
            log { "Service: sendBroadcast" }
            sendBroadcast(Intent(this, PhilosophersStoneReceiver::class.java))
            saveLastLifeTime()
        } else {
            saveLastLifeTime()
        }
        super.onDestroy()
    }
}