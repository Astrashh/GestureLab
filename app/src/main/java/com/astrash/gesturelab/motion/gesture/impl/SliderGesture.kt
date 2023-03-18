package com.astrash.gesturelab.motion.gesture.impl

import com.astrash.gesturelab.motion.Motion
import com.astrash.gesturelab.motion.gesture.Gesture
import com.astrash.gesturelab.util.clamp

class SliderGesture(
    private val liftAction: () -> Unit = {},
    private val getPercent: () -> Float,
    private val setPercent: (Float) -> Unit,
    private val size: Float,
) : Gesture {
    override fun state(motion: Motion, x: Float, y: Float, t: Long): Gesture.State =
        State(
            x, liftAction, getPercent,
            setPercent, size
        )

    private class State(
        private val startX: Float,
        private val liftAction: () -> Unit,
        getPercent: () -> Float,
        private val setPercent: (Float) -> Unit,
        private val size: Float,
    ) : Gesture.State() {

        private val startPercent: Float = getPercent.invoke()

        override fun lift(t: Long) = liftAction.invoke()

        override fun update(x: Float, y: Float, t: Long): Gesture.State {
            val deltaX = startX - x
            val percent = -deltaX / size + startPercent
            setPercent.invoke(clamp(percent, 0f, 1f))
            return this
        }
    }
}
