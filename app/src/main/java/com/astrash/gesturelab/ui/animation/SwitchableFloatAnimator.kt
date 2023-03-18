package com.astrash.gesturelab.ui.animation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator

/*
 * Animates between two different values. Which value the animator targets can be switched while
 * the animation is running.
 */
class SwitchableFloatAnimator(
    private val lowValue: Float,
    private val highValue: Float,
    private val getCurrentValue: () -> Float,
    private val animationDuration: Long,
    private val callback: (Float) -> Unit,
    private val highCallback: () -> Unit,
    private val lowCallback: () -> Unit,
) {
    companion object {
        const val DIRECTION_NONE = 0
        const val DIRECTION_LOW = 1
        const val DIRECTION_HIGH = 2
    }

    private var direction = DIRECTION_NONE
        set(value) {
            when (value) {
                DIRECTION_LOW -> {
                    finishedCallback = lowCallback
                    animator.setFloatValues(getCurrentValue(), lowValue)
                }
                DIRECTION_HIGH -> {
                    finishedCallback = highCallback
                    animator.setFloatValues(getCurrentValue(), highValue)
                }
            }
            field = value
        }

    private var finishedCallback: (() -> Unit)? = null

    private val animator: ValueAnimator = ValueAnimator().apply {
        duration = animationDuration
        addListener(object : AnimatorListenerAdapter() {
            private var cancelled = false
            override fun onAnimationCancel(animation: Animator) {
                cancelled = true
            }

            override fun onAnimationEnd(animation: Animator) {
                if (!cancelled) finishedCallback?.invoke()
                else cancelled = false
            }
        })
        addUpdateListener {
            callback.invoke(this.animatedValue as Float)
        }
    }

    fun transitionHigh() {
        if (direction != DIRECTION_HIGH) {
            animator.cancel()
            direction = DIRECTION_HIGH
            animator.start()
        }
    }

    fun transitionLow() {
        if (direction != DIRECTION_LOW) {
            animator.cancel()
            direction = DIRECTION_LOW
            animator.start()
        }
    }
}
