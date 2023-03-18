package com.astrash.gesturelab.ui.view

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.graphics.*
import android.view.View
import android.view.WindowManager
import com.astrash.gesturelab.extension.screenCoordsToViewCoords
import com.astrash.gesturelab.util.lerp

class TrailScreenOverlay(context: Context) : View(context) {

    private val windowManager: WindowManager =
        context.getSystemService(AccessibilityService.WINDOW_SERVICE) as WindowManager

    private val trailPath = Path()
    private val pathMeasure = PathMeasure()
    private val trailPathPosBuf = FloatArray(2)

    private val trailSteps = 300
    private val trailStepDistance = 3
    private val trailStartRadius = 30f
    private val trailEndRadius = 5f

    private val trailPaint = Paint().apply { color = Color.WHITE }

    private val layoutParameters = WindowManager.LayoutParams().apply {
        type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
        width = WindowManager.LayoutParams.MATCH_PARENT
        height = WindowManager.LayoutParams.MATCH_PARENT
        x = -1 // Apparently setting this to a negative value in combination with
        // MATCH_PARENT lets the view get placed on top of the status bar
        format = PixelFormat.TRANSLUCENT
        flags = flags or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
    }

    private fun show() {
        windowManager.addView(this, layoutParameters)
    }

    private fun hide() {
        windowManager.removeView(this)
    }

    init {
        setBackgroundColor(Color.TRANSPARENT)
        alpha = 0.5f
    }

    fun startTrail(screenX: Float, screenY: Float) {
        show()
        val (viewX, viewY) = screenCoordsToViewCoords(screenX, screenY)
        trailPath.reset()
        trailPath.moveTo(viewX, viewY)
        invalidate()
    }

    fun updateTrail(screenX: Float, screenY: Float) {
        val (viewX, viewY) = screenCoordsToViewCoords(screenX, screenY)
        trailPath.lineTo(viewX, viewY)
        invalidate()
    }

    fun stopTrail() {
        trailPath.reset()
        invalidate()
        hide()
    }

    override fun onDraw(canvas: Canvas) {
        val pathLength = pathMeasure.apply { setPath(trailPath, false) }.length
        for (i in 1..trailSteps) {
            val distance = pathLength - i * trailStepDistance
            if (distance >= 0) {
                pathMeasure.getPosTan(distance, trailPathPosBuf, null)
                val (x, y) = trailPathPosBuf[0] to trailPathPosBuf[1]

                val currTrailRadius =
                    lerp(i.toFloat(), 1f, trailStartRadius, trailSteps.toFloat(), trailEndRadius)

                canvas.drawCircle(x, y, currTrailRadius, trailPaint)
            }
        }
    }
}
