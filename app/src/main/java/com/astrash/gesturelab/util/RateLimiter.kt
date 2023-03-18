package com.astrash.gesturelab.util

class RateLimiter(private val ms: Long) {

    private var timeSinceLast = 0L

    fun execute(l: () -> Unit) {
        val prev = timeSinceLast
        val currTime = System.currentTimeMillis()
        if (currTime - prev > ms) {
            l.invoke()
            timeSinceLast = currTime
        }
    }
}
