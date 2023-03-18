package com.astrash.gesturelab.ui

import android.view.View
import android.view.WindowManager
import com.astrash.gesturelab.ui.animation.SwitchableFloatAnimator
import com.astrash.gesturelab.util.Delayer

class PopupHandler<T : View>(
    private val windowManager: WindowManager,
    private val layoutParameters: WindowManager.LayoutParams,
    val view: T,
    dismissDelay: Long,
) {
    init {
        view.alpha = 0.0f
    }

    private val delayer = Delayer(::dismiss, dismissDelay)

    private val alphaAnimator = SwitchableFloatAnimator(
        0.0f,
        1.0f,
        view::getAlpha,
        300,
        { a -> view.alpha = a },
        {},
        { view.visibility = View.INVISIBLE; windowManager.removeView(view) },
    )

    fun show() {
        if (view.parent == null) {
            view.visibility = View.VISIBLE
            windowManager.addView(view, layoutParameters)
        }
        alphaAnimator.transitionHigh()
        delayer.start()
    }

    fun dismiss() {
        delayer.stop()
        alphaAnimator.transitionLow()
    }

    fun cancelDismiss() = delayer.stop()

    fun scheduleDismiss() = delayer.start()
}
