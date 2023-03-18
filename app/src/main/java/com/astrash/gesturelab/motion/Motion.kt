package com.astrash.gesturelab.motion

import com.astrash.gesturelab.motion.gesture.Gesture

class Motion(
    val manager: MotionManager,
    gesture: Gesture,
    startX: Float,
    startY: Float,
    startTime: Long,
) {
    private var gestureState: Gesture.State = gesture.state(this, startX, startY, startTime)

    fun updateState(x: Float, y: Float, t: Long) {
        gestureState = gestureState.update(x, y, t)
    }

    fun lift(t: Long) {
        gestureState.lift(t)
    }
}
