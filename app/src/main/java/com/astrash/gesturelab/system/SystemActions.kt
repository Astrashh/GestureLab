package com.astrash.gesturelab.system

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.media.AudioManager
import android.media.session.MediaSessionManager
import android.os.*
import android.provider.Settings


class SystemActions(private val service: AccessibilityService) {
    companion object {
        const val TAG = "SystemActions"
    }

    // TODO - Move related actions into their own classes

    val brightness = BrightnessController(service.contentResolver)

    val vibrator = VibrationController(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            (service.getSystemService(AccessibilityService.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        else
            service.getSystemService(AccessibilityService.VIBRATOR_SERVICE) as Vibrator
    )

    val media = MediaController(
        service,
        service.getSystemService(AccessibilityService.AUDIO_SERVICE) as AudioManager,
        service.getSystemService(AccessibilityService.MEDIA_SESSION_SERVICE) as MediaSessionManager
    )

    fun back() =
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)

    fun home() =
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME)

    fun openNotifications() =
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS)

    fun openRecents() =
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS)

    fun openQuickSettings() =
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_QUICK_SETTINGS)

    fun openAccessibilitySettings() =
        service.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))

    fun openSettings() =
        service.startActivity(Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
}
