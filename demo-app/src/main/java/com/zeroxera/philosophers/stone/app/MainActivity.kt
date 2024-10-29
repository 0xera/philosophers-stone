package com.zeroxera.philosophers.stone.app

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.zeroxera.philosophers.stone.PhilosophersStone

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "Activity: onCreate() called with: savedInstanceState = $savedInstanceState")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content)
        findViewById<TextView>(R.id.app_id).text = "Current app id: ${MyApp.ID}"
        findViewById<TextView>(R.id.prev_duration).text = "Prev app life duration: ${PhilosophersStone.retrieveLastLifeDuration() / 1000L} seconds"
    }
}