package com.astrash.gesturelab.ui.view

import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.WindowManager

class EdgePanel(context: Context, private val edge: Edge) : View(context) {

    private val windowManager: WindowManager =
        context.getSystemService(WINDOW_SERVICE) as WindowManager

    // TODO - Data driven edge placement
    private val layoutParameters: WindowManager.LayoutParams
        get() = WindowManager.LayoutParams().apply {
            type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
            format = PixelFormat.TRANSLUCENT
            width = 50
            height = 2000
            x = -1
            flags = flags or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
            gravity = Gravity.CENTER_VERTICAL or when (edge) {
                Edge.LEFT -> Gravity.START
                Edge.RIGHT -> Gravity.END
            }
        }

    fun show() {
        windowManager.addView(this, layoutParameters)
    }

    enum class Edge {
        LEFT, RIGHT
    }
}
