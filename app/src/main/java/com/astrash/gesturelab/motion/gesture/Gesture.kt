package com.astrash.gesturelab.motion.gesture

import com.astrash.gesturelab.motion.Motion

fun interface Gesture {
    fun state(motion: Motion, x: Float, y: Float, t: Long): State

    abstract class State {
        companion object {
            val NoOp = NoOpGesture()
        }

        open fun update(x: Float, y: Float, t: Long): State = this

        open fun lift(t: Long) = Unit

        class NoOpGesture : Gesture {
            override fun state(motion: Motion, x: Float, y: Float, t: Long): State = object : State() {}
        }
    }
}
