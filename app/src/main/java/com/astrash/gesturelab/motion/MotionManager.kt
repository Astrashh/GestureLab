package com.astrash.gesturelab.motion

import android.annotation.SuppressLint
import android.os.Build
import android.view.MotionEvent
import android.view.VelocityTracker
import com.astrash.gesturelab.motion.gesture.*
import com.astrash.gesturelab.motion.gesture.impl.*

class MotionManager(private val motionGesture: Gesture) {
    companion object {
        const val POINTER_ID = 0
    }

    private var currentMotion: Motion? = null
    var velocityTracker: VelocityTracker? = null

    var lastTapTime: Long? = null
    private var consecutiveTaps = 0

    var motionStartListener: OnMotionStartListener? = null
    var motionMoveListener: OnMotionMoveListener? = null
    var motionEndListener: OnMotionEndListener? = null

    @SuppressLint("Recycle")
    fun consumeMotionEvent(event: MotionEvent?): Boolean {
        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                velocityTracker?.clear()
                velocityTracker = velocityTracker ?: VelocityTracker.obtain()
                velocityTracker?.addMovement(event)
                val (x, y) = getPointerPos(event)
                motionStartListener?.callback(x, y)
                currentMotion = Motion(this, motionGesture, x, y, event.eventTime)
            }
            MotionEvent.ACTION_MOVE -> {
                velocityTracker?.addMovement(event)
                val (x, y) = getPointerPos(event)
                motionMoveListener?.callback(x, y)
                currentMotion?.updateState(x, y, event.eventTime)
            }
            MotionEvent.ACTION_POINTER_UP -> if (event.getPointerId(event.actionIndex) == POINTER_ID) stopMotion(
                event.eventTime
            )
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> stopMotion(event.eventTime)
        }
        return true
    }

    private fun getPointerPos(event: MotionEvent): Pair<Float, Float> =
        event.findPointerIndex(POINTER_ID).let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                event.getRawX(it) to event.getRawY(it)
            } else {
                event.rawX to event.rawY // Different method of getting raw coords should implemented used if motions ever utilize any non 0 pointer id
            }
        }

    private fun stopMotion(t: Long) {
        currentMotion?.lift(t)
        currentMotion = null
        velocityTracker?.recycle()
        velocityTracker = null
        motionEndListener?.callback()
    }

    fun getPointerVelocity(): Pair<Float, Float> {
        velocityTracker!!.let {
            it.computeCurrentVelocity(1)
            return it.getXVelocity(POINTER_ID) to it.getYVelocity(POINTER_ID)
        }
    }

    fun hasActiveMotion(): Boolean = currentMotion != null

    fun addTap() = consecutiveTaps++

    fun resetConsecutiveTaps() {
        consecutiveTaps = 0
    }

    fun getConsecutiveTaps() = consecutiveTaps

    fun interface CoordinateListener {
        fun callback(x: Float, y: Float)
    }

    fun interface OnMotionStartListener : CoordinateListener

    fun interface OnMotionMoveListener : CoordinateListener

    fun interface OnMotionEndListener {
        fun callback()
    }
}
