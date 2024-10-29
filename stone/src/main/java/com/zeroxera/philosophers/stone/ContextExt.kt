package com.zeroxera.philosophers.stone

import android.content.Context
import android.content.SharedPreferences

internal val Context.sharedPreferences: SharedPreferences
    get() = getSharedPreferences("PhilosophersStonePref", Context.MODE_PRIVATE)