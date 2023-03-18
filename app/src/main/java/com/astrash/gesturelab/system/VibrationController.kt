package com.astrash.gesturelab.system

import android.os.Build
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.Vibrator

class VibrationController(private val vibrator: Vibrator) {
    fun click() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            vibrator.vibrate(
                VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK),
                VibrationAttributes.createForUsage(VibrationAttributes.USAGE_ACCESSIBILITY)
            )
        } else {
            // TODO
        }
    }

    fun doubleClick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            vibrator.vibrate(
                VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK),
                VibrationAttributes.createForUsage(VibrationAttributes.USAGE_ACCESSIBILITY)
            )
        } else {
            // TODO
        }
    }

    fun heavyClick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            vibrator.vibrate(
                VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK),
                VibrationAttributes.createForUsage(VibrationAttributes.USAGE_ACCESSIBILITY)
            )
        } else {
            // TODO
        }
    }
}
