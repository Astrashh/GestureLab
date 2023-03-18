package com.astrash.gesturelab.motion.gesture.impl

import com.astrash.gesturelab.motion.Motion
import com.astrash.gesturelab.motion.gesture.Gesture
import com.astrash.gesturelab.util.magnitude

class WaitStopGesture(
    private val triggerBelowVelocity: Float,
    val next: Gesture,
) : Gesture {
    override fun state(motion: Motion, x: Float, y: Float, t: Long): Gesture.State =
        State(
            motion,
            triggerBelowVelocity,
            next
        )

    private class State(
        private val motion: Motion,
        private val triggerBelowVelocity: Float,
        private val next: Gesture,
    ) : Gesture.State() {

        override fun update(x: Float, y: Float, t: Long): Gesture.State {
            return if (magnitude(motion.manager.getPointerVelocity()) < triggerBelowVelocity) next.state(
                motion,
                x,
                y,
                t
            )
            else this
        }
    }
}
