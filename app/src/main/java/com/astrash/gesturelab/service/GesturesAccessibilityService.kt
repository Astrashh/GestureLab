package com.astrash.gesturelab.service

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.view.*
import android.view.accessibility.AccessibilityEvent
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.astrash.gesturelab.MainGestures
import com.astrash.gesturelab.R
import com.astrash.gesturelab.geometry.Point
import com.astrash.gesturelab.motion.MotionManager
import com.astrash.gesturelab.slider.PopupSlider
import com.astrash.gesturelab.system.SystemActions
import com.astrash.gesturelab.ui.PopupHandler
import com.astrash.gesturelab.ui.view.EdgePanel
import com.astrash.gesturelab.ui.view.Selector
import com.astrash.gesturelab.ui.view.PillSeekBar
import com.astrash.gesturelab.ui.view.TrailScreenOverlay
import com.astrash.gesturelab.util.AppInfo
import com.astrash.gesturelab.util.PackageUtils
import com.astrash.gesturelab.util.getBitmapFromDrawable

class GesturesAccessibilityService : AccessibilityService() {

    companion object {
        const val SHOW_PANELS = false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onServiceConnected() {
        startService(Intent(this, MediaNotificationService::class.java))

        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val actions = SystemActions(this)

        // TODO - Break up logic for this method and move elsewhere

        val trailOverlay = TrailScreenOverlay(this)

        val leftPanel = EdgePanel(this, EdgePanel.Edge.LEFT)
        val rightPanel = EdgePanel(this, EdgePanel.Edge.RIGHT)

        if (SHOW_PANELS) {
            leftPanel.setBackgroundColor(Color.WHITE)
            rightPanel.setBackgroundColor(Color.WHITE)
            leftPanel.alpha = 0.1f
            rightPanel.alpha = 0.1f
        }

        val sliderLayoutParams = WindowManager.LayoutParams().apply {
            type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
            format = PixelFormat.TRANSLUCENT
            gravity = Gravity.CENTER_HORIZONTAL
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            y = 200
            flags = flags or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
        }

        val brightnessSlider = PopupSlider(
            PillSeekBar(
                this,
                VectorDrawableCompat.create(
                    resources,
                    R.drawable.brightness_low_fill0_wght700_grad0_opsz48,
                    null
                )
            ),
            windowManager,
            WindowManager.LayoutParams()
                .apply { copyFrom(sliderLayoutParams); gravity = gravity or Gravity.BOTTOM },
            actions.brightness::setPercent,
            actions.vibrator::heavyClick,
            actions.vibrator::heavyClick,
            1000 / 25 // 25 per second
        )

        val volumeSlider = PopupSlider(
            PillSeekBar(
                this,
                VectorDrawableCompat.create(
                    resources,
                    R.drawable.music_note_fill0_wght700_grad0_opsz48,
                    null
                )
            ),
            windowManager,
            WindowManager.LayoutParams()
                .apply { copyFrom(sliderLayoutParams); gravity = gravity or Gravity.TOP },
            actions.media::setVolume,
            actions.vibrator::heavyClick,
            actions.vibrator::heavyClick,
            1000 / 15 // 15 per second
        )

        val mediaSeekSlider = PopupSlider(
            PillSeekBar(this, null),
            windowManager,
            WindowManager.LayoutParams()
                .apply { copyFrom(sliderLayoutParams); gravity = gravity or Gravity.TOP },
            actions.media::seek,
            actions.vibrator::heavyClick,
            { actions.vibrator.heavyClick(); actions.media.pause() },
            1000 / 5 // 5 per second
        )

        // TODO - Un-hardcode selector creation with selector editor
        val apps = PackageUtils.getAppInfoList(packageManager)
        val getByPackage = { name: CharSequence, list: List<AppInfo> -> list.find { it.packageName == name } }
        val firefox = getByPackage("org.mozilla.firefox", apps)!!
        val spotify = getByPackage("com.spotify.music", apps)!!
        val discord = getByPackage("com.discord", apps)!!
        val element = getByPackage("im.vector.app", apps)!!
        val dist = 300f
        val selector = PopupHandler(
            windowManager,
            WindowManager.LayoutParams().apply {
                type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = WindowManager.LayoutParams.MATCH_PARENT
                x = -1 // Apparently setting this to a negative value in combination with
                // MATCH_PARENT lets the view get placed on top of the status bar
                format = PixelFormat.TRANSLUCENT
                flags = flags or
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                        WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
            },
            Selector(
                this,
                Point(0f, 0f),
                listOf(
                    Selector.Selection({ firefox.launch(this, packageManager) }, getBitmapFromDrawable(firefox.icon), Point(dist, 0f)),
                    Selector.Selection({ spotify.launch(this, packageManager) }, getBitmapFromDrawable(spotify.icon), Point(-dist, 0f)),
                    Selector.Selection({ discord.launch(this, packageManager) }, getBitmapFromDrawable(discord.icon), Point(0f, dist)),
                    Selector.Selection({ element.launch(this, packageManager) }, getBitmapFromDrawable(element.icon), Point(0f, -dist)),
                )
            ),
            5000L
        )

        val mainStateBuilder =
            MainGestures.get(
                actions,
                brightnessSlider,
                volumeSlider,
                mediaSeekSlider,
                selector
            )

        val motionManager = MotionManager(mainStateBuilder)

        leftPanel.setOnTouchListener { _, event -> motionManager.consumeMotionEvent(event) }
        rightPanel.setOnTouchListener { _, event -> motionManager.consumeMotionEvent(event) }

        motionManager.motionStartListener =
            MotionManager.OnMotionStartListener(trailOverlay::startTrail)
        motionManager.motionMoveListener =
            MotionManager.OnMotionMoveListener(trailOverlay::updateTrail)
        motionManager.motionEndListener = MotionManager.OnMotionEndListener(trailOverlay::stopTrail)

        leftPanel.show()
        rightPanel.show()
    }

    override fun onInterrupt() {
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
    }

    override fun onUnbind(intent: Intent?): Boolean {
        stopService(Intent(this, MediaNotificationService::class.java))
        return super.onUnbind(intent)
    }
}
