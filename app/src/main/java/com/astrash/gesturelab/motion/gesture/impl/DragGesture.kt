package com.astrash.gesturelab.motion.gesture.impl

import com.astrash.gesturelab.motion.Motion
import com.astrash.gesturelab.motion.gesture.Gesture
import com.astrash.gesturelab.motion.gesture.Gesture.State.Companion.NoOp
import com.astrash.gesturelab.motion.gesture.impl.DragGesture.State.Companion.DEFAULT_SIDE_DISTANCE
import com.astrash.gesturelab.motion.gesture.impl.DragGesture.State.Companion.DEFAULT_VERTICAL_DISTANCE
import kotlin.math.abs

data class DragGesture(
    var side: Gesture = NoOp,
    var sideDistance: Float = DEFAULT_SIDE_DISTANCE,
    var up: Gesture = NoOp,
    var upDistance: Float = DEFAULT_VERTICAL_DISTANCE,
    var down: Gesture = NoOp,
    var downDistance: Float = DEFAULT_VERTICAL_DISTANCE,
) : Gesture {

    override fun state(motion: Motion, x: Float, y: Float, t: Long): Gesture.State =
        State(
            motion, x, y, side,
            sideDistance, up,
            upDistance, down,
            downDistance,
        )

    fun side(distance: Float, builder: Gesture) =
        apply { side = builder; sideDistance = distance }

    fun up(distance: Float, builder: Gesture) =
        apply { up = builder; upDistance = distance }

    fun down(distance: Float, builder: Gesture) =
        apply { down = builder; downDistance = distance }

    private class State(
        private val motion: Motion,
        private val startX: Float,
        private val startY: Float,
        private val side: Gesture,
        private val sideDistance: Float,
        private val up: Gesture,
        private val upDistance: Float,
        private val down: Gesture,
        private val downDistance: Float,
    ) : Gesture.State() {

        companion object {
            const val DEFAULT_VERTICAL_DISTANCE = 400f
            const val DEFAULT_SIDE_DISTANCE = 100f
        }

        override fun update(x: Float, y: Float, t: Long): Gesture.State {
            // TODO - Abstract direction selection
            val dx = startX - x
            val dy = startY - y
            return if (abs(dx) > sideDistance && side != NoOp) {
                side.state(motion, x, y, t)
            } else if (dy < -downDistance) {
                down.state(motion, x, y, t)
            } else if (dy > upDistance) {
                up.state(motion, x, y, t)
            } else this
        }
    }
}
