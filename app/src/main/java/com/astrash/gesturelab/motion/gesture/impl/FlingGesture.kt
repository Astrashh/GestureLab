package com.astrash.gesturelab.motion.gesture.impl

import android.util.Log
import com.astrash.gesturelab.extension.applyIfTrue
import com.astrash.gesturelab.motion.Motion
import com.astrash.gesturelab.motion.gesture.Gesture
import com.astrash.gesturelab.motion.gesture.Gesture.State.Companion.NoOp
import com.astrash.gesturelab.util.magnitude
import com.astrash.gesturelab.util.radToDeg
import kotlin.math.abs
import kotlin.math.atan2

data class FlingGesture(
    var downAction: () -> Unit = {},
    var upAction: () -> Unit = {},
    var sideAction: () -> Unit = {},
    var dragged: Gesture = NoOp,
) : Gesture {
    override fun state(motion: Motion, x: Float, y: Float, t: Long): Gesture.State =
        State(
            motion, x, y, t,
            downAction, upAction, sideAction, dragged
        )

    fun down(action: () -> Unit) = apply { downAction = action }

    fun up(action: () -> Unit) = apply { upAction = action }

    fun side(action: () -> Unit) = apply { sideAction = action }

    fun otherwise(builder: Gesture) = apply { dragged = builder }

    private class State(
        private val motion: Motion,
        private val startX: Float,
        private val startY: Float,
        private val startTime: Long,
        private val downAction: () -> Unit,
        private val upAction: () -> Unit,
        private val sideAction: () -> Unit,
        private val next: Gesture,
    ) : Gesture.State() {
        companion object {
            const val TAG = "FlingState"
            const val TIME_WINDOW = 200
            const val MIN_VELOCITY = 1.5
        }

        override fun lift(t: Long) {
            val (vx, vy) = motion.manager.getPointerVelocity()
            val mag = magnitude(vx, vy)
            if (mag > MIN_VELOCITY) {
                // TODO - Abstract angle actions
                val angle = radToDeg(atan2(vx, vy))
                if (abs(angle) < 45) {
                    downAction.invoke()
                    Log.d(TAG, "Fling down (Speed: $mag Angle: $angle)")
                } else if (abs(angle) >= 135) {
                    upAction.invoke()
                    Log.d(TAG, "Fling up (Speed: $mag Angle: $angle)")
                } else {
                    sideAction.invoke()
                    Log.d(TAG, "Fling side (Speed: $mag Angle: $angle)")
                }
            } else {
                Log.d(TAG, "Fling velocity not great enough (Speed: $mag)")
            }
        }

        override fun update(x: Float, y: Float, t: Long): Gesture.State {
            val (vx, vy) = motion.manager.getPointerVelocity()
            val mag = magnitude(vx, vy)
            return if (
                (t - startTime > TIME_WINDOW).applyIfTrue {
                    Log.d(
                        TAG,
                        "Fling took too long, moving to next state"
                    )
                } ||
                (mag < MIN_VELOCITY).applyIfTrue {
                    Log.d(
                        TAG,
                        "Velocity too low, moving to next state"
                    )
                }
            )
                next.state(motion, startX, startY, t)
            else this
        }
    }
}

