package com.zeroxera.philosophers.stone

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.zeroxera.philosophers.stone.PhilosophersStoneInternal.Worker
import com.zeroxera.philosophers.stone.PhilosophersStoneInternal.isForeground
import com.zeroxera.philosophers.stone.PhilosophersStoneInternal.log
import com.zeroxera.philosophers.stone.PhilosophersStoneInternal.mainHandler
import com.zeroxera.philosophers.stone.PhilosophersStoneInternal.saveLastLifeTime
import com.zeroxera.philosophers.stone.PhilosophersStoneInternal.worker

internal class PhilosophersStoneReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (!PhilosophersStoneInternal.isEnabled) return

        log { "BroadcastReceiver: onReceive" }

        if (intent.action == Intent.ACTION_BOOT_COMPLETED && shouldUseService()) {
            log { "BroadcastReceiver: start service for ACTION_BOOT_COMPLETED" }
            context.startService(Intent(context, PhilosophersStoneService::class.java))
            return
        }

        if (!isForeground) {
            worker = Worker.BroadcastReceiver
            log { "BroadcastReceiver: wait" }
            for (i in 0 until 80) { // 10s before anr
                if (isForeground) break
                if (mainHandler.looper.queue.isIdle) {
                    Thread.sleep(100)
                } else {
                    mainHandler.looper.queue.addIdleHandler {
                        if (!isForeground) {
                            log { "BroadcastReceiver: send broadcast from IdleHandler" }
                            context.sendBroadcast(Intent(context, PhilosophersStoneReceiver::class.java))
                            saveLastLifeTime()
                        }
                        false // remove after invoke
                    }
                    saveLastLifeTime()
                    return
                }
            }
            if (!isForeground) {
                log { "BroadcastReceiver: send broadcast" }
                context.sendBroadcast(Intent(context, PhilosophersStoneReceiver::class.java))
                saveLastLifeTime()
            } else {
                saveLastLifeTime()
            }
        }
    }
}


