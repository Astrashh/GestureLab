package com.astrash.gesturelab.slider

import android.annotation.SuppressLint
import android.graphics.drawable.*
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.astrash.gesturelab.ui.PopupHandler
import com.astrash.gesturelab.util.RateLimiter
import kotlin.math.roundToInt

@SuppressLint("ClickableViewAccessibility")
class PopupSlider(
    private val seekBar: SeekBar,
    windowManager: WindowManager,
    layoutParameters: WindowManager.LayoutParams,
    private val percentControl: (Float) -> Unit,
    private val startReachedAction: () -> Unit = {},
    private val endReachedAction: () -> Unit = {},
    percentControlRateLimit: Long? = null,
) {
    init {
        seekBar.apply {
            setOnTouchListener { _: View, event: MotionEvent? ->
                when (event?.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        popupHandler.cancelDismiss()
                        popupHandler.show()
                        true
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        popupHandler.scheduleDismiss()
                        true
                    }
                    MotionEvent.ACTION_OUTSIDE -> {
                        popupHandler.dismiss()
                        true
                    }
                    else -> false
                }
            }
            setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    when (val percent = progress.toFloat() / max.toFloat()) {
                        1.0f, 0.0f -> {
                            if (!endReached) {
                                endReached = true
                                if (percent == 1.0f) endReachedAction.invoke()
                                else startReachedAction.invoke()
                            }
                            percentControl(percent) // Don't rate limit at slider ends
                        }
                        else -> {
                            endReached = false
                            rateLimitedPercentControl(percent)
                        }
                    }
                }
            })
        }
    }

    private var endReached = false

    private val popupHandler = PopupHandler(windowManager, layoutParameters, seekBar, 5000L)

    private val rateLimitedPercentControl =
        percentControlRateLimit
            ?.let {
                val rateLimiter =
                    RateLimiter(it); { p -> rateLimiter.execute { percentControl(p) } }
            }
            ?: percentControl

    fun set(percent: Float) {
        seekBar.apply { progress = (percent * max).roundToInt() }
        popupHandler.show()
    }
}
