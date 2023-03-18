package com.astrash.gesturelab.motion.gesture.impl

import com.astrash.gesturelab.motion.Motion
import com.astrash.gesturelab.motion.gesture.Gesture
import com.astrash.gesturelab.motion.gesture.Gesture.State.Companion.NoOp

data class RunGesture(
    private val action: () -> Unit,
    private var then: Gesture = NoOp,
) : Gesture {
    override fun state(motion: Motion, x: Float, y: Float, t: Long): Gesture.State =
        State(
            motion,
            action,
            then
        )

    fun then(next: Gesture) = apply { then = next }

    private class State(
        private val motion: Motion,
        private val action: () -> Unit,
        private val then: Gesture,
    ) : Gesture.State() {

        override fun update(x: Float, y: Float, t: Long): Gesture.State {
            action.invoke()
            return then.state(motion, x, y, t)
        }
    }
}
