package com.zeroxera.philosophers.stone

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.zeroxera.philosophers.stone.PhilosophersStoneInternal.Worker

internal class PhilosophersStoneReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (!PhilosophersStoneInternal.isEnabled) return

        PhilosophersStoneInternal.log { "BroadcastReceiver: onReceive" }

        if (intent.action == Intent.ACTION_BOOT_COMPLETED && shouldUseService()) {
            PhilosophersStoneInternal.log { "BroadcastReceiver: start service for ACTION_BOOT_COMPLETED" }
            context.startService(Intent(context, PhilosophersStoneService::class.java))
            return
        }

        if (!PhilosophersStoneInternal.isForeground) {
            PhilosophersStoneInternal.worker = Worker.BroadcastReceiver
            PhilosophersStoneInternal.log { "BroadcastReceiver: wait" }
            for (i in 0 until 80) { // 10s before anr
                if (PhilosophersStoneInternal.isForeground) break
                if (PhilosophersStoneInternal.mainHandler.looper.queue.isIdle) {
                    Thread.sleep(100)
                } else {
                    PhilosophersStoneInternal.mainHandler.looper.queue.addIdleHandler {
                        if (!PhilosophersStoneInternal.isForeground) {
                            PhilosophersStoneInternal.log { "BroadcastReceiver: send broadcast from IdleHandler" }
                            context.sendBroadcast(Intent(context, PhilosophersStoneReceiver::class.java))
                            PhilosophersStoneInternal.saveLastLifeTime()
                        }
                        false // remove after invoke
                    }
                    PhilosophersStoneInternal.saveLastLifeTime()
                    return
                }
            }
            if (!PhilosophersStoneInternal.isForeground) {
                PhilosophersStoneInternal.log { "BroadcastReceiver: send broadcast" }
                context.sendBroadcast(Intent(context, PhilosophersStoneReceiver::class.java))
                PhilosophersStoneInternal.saveLastLifeTime()
            } else {
                PhilosophersStoneInternal.saveLastLifeTime()
            }
        }
    }
}


