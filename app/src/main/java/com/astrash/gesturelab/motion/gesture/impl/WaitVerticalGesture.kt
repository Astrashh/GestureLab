package com.astrash.gesturelab.motion.gesture.impl

import com.astrash.gesturelab.motion.Motion
import com.astrash.gesturelab.motion.gesture.Gesture
import com.astrash.gesturelab.motion.gesture.Gesture.State.Companion.NoOp
import kotlin.math.abs

class WaitVerticalGesture(
    private val distance: Float,
    val next: Gesture = NoOp,
) : Gesture {

    override fun state(motion: Motion, x: Float, y: Float, t: Long): Gesture.State =
        State(
            motion,
            x,
            distance,
            next
        )

    private class State(
        private val motion: Motion,
        private val startX: Float,
        private val distance: Float,
        private val next: Gesture,
    ) : Gesture.State() {

        override fun update(x: Float, y: Float, t: Long): Gesture.State {
            val dx = startX - x
            return if (abs(dx) > distance) next.state(motion, x, y, t)
            else this
        }
    }
}
