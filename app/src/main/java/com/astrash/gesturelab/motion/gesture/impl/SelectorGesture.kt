package com.astrash.gesturelab.motion.gesture.impl

import com.astrash.gesturelab.motion.Motion
import com.astrash.gesturelab.motion.gesture.Gesture
import com.astrash.gesturelab.ui.PopupHandler
import com.astrash.gesturelab.ui.view.Selector

class SelectorGesture(private val selector: PopupHandler<Selector<() -> Unit>>) : Gesture {
    override fun state(motion: Motion, x: Float, y: Float, t: Long): Gesture.State =
        State(x, y, selector)

    private class State(
        startX: Float,
        startY: Float,
        private val selector: PopupHandler<Selector<() -> Unit>>
    ) : Gesture.State() {

        init {
            selector.view.setCenter(startX, startY)
            selector.show()
            selector.cancelDismiss()
        }

        override fun update(x: Float, y: Float, t: Long): Gesture.State {
            selector.view.setPosition(x, y)
            return this
        }

        override fun lift(t: Long) {
            selector.dismiss()
            selector.view.select()?.invoke()
        }
    }
}
