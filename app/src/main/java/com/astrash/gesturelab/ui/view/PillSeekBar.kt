package com.astrash.gesturelab.ui.view

import android.content.Context
import android.graphics.drawable.*
import android.view.Gravity
import com.astrash.gesturelab.R

class PillSeekBar(context: Context, startIcon: Drawable?) :
    androidx.appcompat.widget.AppCompatSeekBar(context) {

    private val height = 150
    private val border = 25

    private val outerRadius = height / 2
    private val innerRad = outerRadius - border
    private val innerHeight = innerRad * 2

    private val foregroundColor = resources.getColor(R.color.brightness_slider_foreground, null)
    private val backgroundColor = resources.getColor(R.color.brightness_slider_background, null)

    private val backgroundDrawable = GradientDrawable(null, null).apply {
        shape = GradientDrawable.RECTANGLE
        setColor(backgroundColor)
        setSize(-1, height)
        cornerRadius = outerRadius.toFloat()
    }

    private val foregroundStartDrawable = InsetDrawable(
        GradientDrawable(null, null).apply {
            mutate()
            shape = GradientDrawable.RECTANGLE
            setColor(foregroundColor)
            setSize(innerRad, -1)
            val innerRad = innerRad.toFloat()
            cornerRadii = floatArrayOf(innerRad, innerRad, 0f, 0f, 0f, 0f, innerRad, innerRad)
        },
        border
    )

    private val foregroundDrawable = InsetDrawable(
        ClipDrawable(
            GradientDrawable(null, null).apply {
                mutate()
                shape = GradientDrawable.RECTANGLE
                setColor(foregroundColor)
            },
            Gravity.START,
            ClipDrawable.HORIZONTAL
        ),
        outerRadius - border, 0, outerRadius - border, 0
    )

    private val sliderProgressDrawable = LayerDrawable(
        arrayOf(
            backgroundDrawable,
            foregroundStartDrawable,
            foregroundDrawable,
        )
    ).apply { setLayerGravity(1, Gravity.START) }

    private val thumbBackgroundDrawable = GradientDrawable(null, null).apply {
        shape = GradientDrawable.OVAL
        setColor(foregroundColor)
        setSize(innerHeight, innerHeight)
    }

    private val iconContainerDrawable = InsetDrawable(GradientDrawable(), 10)

    private fun setIcon(drawable: Drawable?) {
        drawable?.apply { setTint(backgroundColor); iconContainerDrawable.drawable = this }
    }

    init {
        progressDrawable = sliderProgressDrawable
        setIcon(startIcon)
        thumb = LayerDrawable(arrayOf(thumbBackgroundDrawable, iconContainerDrawable))
        thumbOffset = -border
        splitTrack = false
        max = 1000
        setPadding(100, 0, 100, 0)
    }
}
