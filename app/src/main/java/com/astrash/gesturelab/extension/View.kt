package com.astrash.gesturelab.extension

import android.view.View

fun View.screenCoordsToViewCoords(screenX: Float, screenY: Float): Pair<Float, Float> {
    val viewPosBuf = IntArray(2)
    getLocationOnScreen(viewPosBuf)
    return screenX - viewPosBuf[0] to screenY - viewPosBuf[1]
}