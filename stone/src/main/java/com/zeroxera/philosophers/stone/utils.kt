package com.zeroxera.philosophers.stone

import android.os.Build

// services can be used for android 14 too, but for latest releases.
// suddenly, we can't check what release we are using now
internal fun shouldUseService() = Build.VERSION.SDK_INT >= 35