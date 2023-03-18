package com.astrash.gesturelab.motion.gesture.impl

import android.os.Handler
import android.os.Looper
import com.astrash.gesturelab.motion.Motion
import com.astrash.gesturelab.motion.gesture.Gesture
import com.astrash.gesturelab.util.distance

data class TapGesture(
    val dragStates: List<Gesture> = emptyList(),
    val tapCompleteActions: List<() -> Unit> = emptyList(),
    val holdCompleteActions: List<() -> Unit> = emptyList(),
    val onHoldActions: List<() -> Unit> = emptyList(),
) : Gesture {
    override fun state(motion: Motion, x: Float, y: Float, t: Long): Gesture.State =
        State(
            motion, x, y, t,
            dragStates, tapCompleteActions, holdCompleteActions, onHoldActions
        )

    private class State(
        private val motion: Motion,
        private val startX: Float,
        private val startY: Float,
        private val startTime: Long,
        private val dragStates: List<Gesture>,
        private val tapCompleteActions: List<() -> Unit>,
        private val holdCompleteActions: List<() -> Unit>,
        private val onHoldActions: List<() -> Unit>,
    ) : Gesture.State() {
        companion object {
            const val TOUCH_SLOP = 30
            const val HOLD_TIME = 150L
            const val CONSECUTIVE_TAP_DELAY = 300L
        }

        private var longHold = false

        init {
            motion.manager.lastTapTime?.also {
                val deltaT = startTime - it
                if (deltaT > CONSECUTIVE_TAP_DELAY) {
                    motion.manager.resetConsecutiveTaps()
                }
            }
            motion.manager.addTap()
            motion.manager.lastTapTime = startTime
        }

        override fun update(x: Float, y: Float, t: Long): Gesture.State {
            val taps = motion.manager.getConsecutiveTaps()
            if (!longHold && t - startTime > HOLD_TIME) {
                longHold = true
                onHoldActions.getOrNull(taps - 1)?.invoke()
            }
            return if (distance(x, y, startX, startY) > TOUCH_SLOP) {
                motion.manager.resetConsecutiveTaps()
                return (dragStates.getOrNull(taps - 1) ?: NoOp).state(motion, x, y, t)
            } else this
        }

        private fun runIfLastTap(taps: Int, action: () -> Unit) =
            Handler(Looper.getMainLooper()).postDelayed(
                { if (taps == motion.manager.getConsecutiveTaps()) action.invoke() },
                CONSECUTIVE_TAP_DELAY
            )

        override fun lift(t: Long) {
            val taps = motion.manager.getConsecutiveTaps()
            val stateTime = t - startTime
            if (stateTime > HOLD_TIME) {
                if (taps == holdCompleteActions.size) holdCompleteActions[taps - 1].invoke()
                else runIfLastTap(taps) {
                    holdCompleteActions.getOrNull(taps - 1)?.invoke()
                    if (!motion.manager.hasActiveMotion()) motion.manager.resetConsecutiveTaps()
                }
            } else {
                if (taps == tapCompleteActions.size) tapCompleteActions[taps - 1].invoke()
                else runIfLastTap(taps) {
                    tapCompleteActions.getOrNull(taps - 1)?.invoke()
                    if (!motion.manager.hasActiveMotion()) motion.manager.resetConsecutiveTaps()
                }
            }
        }
    }
}
