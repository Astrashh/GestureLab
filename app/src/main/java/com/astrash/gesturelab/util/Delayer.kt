package com.astrash.gesturelab.util

import android.os.Handler
import android.os.Looper

class Delayer(
    delayedAction: () -> Unit,
    private val delay: Long,
) {
    private val handler = Handler(Looper.getMainLooper())

    private val runnable = Runnable(delayedAction)

    fun stop() = handler.removeCallbacks(runnable)

    fun start() { stop(); handler.postDelayed(runnable, delay) }
}
