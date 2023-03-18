package com.astrash.gesturelab.system

import android.content.ContentResolver
import android.provider.Settings
import com.astrash.gesturelab.util.pow2
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.roundToInt
import kotlin.math.sqrt

class BrightnessController(private val contentResolver: ContentResolver) {

    fun getPercent(): Float {
        val brightness = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS)
        return BrightnessUtils.brightnessToPercent(
            brightness,
            getMinBrightness(),
            getMaxBrightness()
        )
    }

    fun setPercent(percent: Float) {
        val brightness =
            BrightnessUtils.percentToBrightness(percent, getMinBrightness(), getMaxBrightness())
        // Ideally should use Settings.System.SCREEN_BRIGHTNESS_FLOAT but that doesn't appear to be available to modify this way
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightness)
    }

    private fun getMaxBrightness() =
        128 // According to documentation this should be 255 but in testing with a Pixel 6 Pro on Android 13 it caps around 128?

    private fun getMinBrightness() = 1

    class BrightnessUtils {
        companion object {

            /*
             * Converts perceptual percentage of brightness into the scaled linear format system settings uses.
             */
            fun percentToBrightness(percent: Float, minBrightness: Int, maxBrightness: Int): Int {
                val linearPercent = perceptualPercentToLinearPercent(percent)
                return scalePercentToBrightness(linearPercent, minBrightness, maxBrightness)
            }

            /*
             * Converts system settings brightness into a perceptual percentage of brightness.
             */
            fun brightnessToPercent(
                brightness: Int,
                minBrightness: Int,
                maxBrightness: Int
            ): Float {
                val linearPercent =
                    scaleBrightnessToPercent(brightness, minBrightness, maxBrightness)
                return linearPercentToPerceptualPercent(linearPercent)
            }

            private fun scalePercentToBrightness(
                percent: Float,
                minBrightness: Int,
                maxBrightness: Int
            ): Int =
                (percent * (maxBrightness - minBrightness) + minBrightness + 0.499f).roundToInt()

            private fun scaleBrightnessToPercent(
                brightness: Int,
                minBrightness: Int,
                maxBrightness: Int
            ): Float =
                (brightness - minBrightness).toFloat() / (maxBrightness - minBrightness).toFloat()

            /*
             * Hybrid log-gamma conversion functions, converts back and forth between linear values
             * (as stored in system settings) and gamma values. Gamma values better match human perception
             * of brightness, and is perceived as linear rather than the true linear values that the system handles.
             */
            private const val A = 0.17883277f
            private const val B = 0.28466892f
            private const val C = 0.5599107f

            private fun perceptualPercentToLinearPercent(a: Float): Float {
                return if (a < 0.5)
                    pow2(a) / 3f
                else
                    ((exp(((a - C) / A).toDouble()) + B) / 12f).toFloat()
            }

            private fun linearPercentToPerceptualPercent(a: Float): Float {
                return if (a < 1f / 12f)
                    sqrt(3f * a)
                else
                    A * ln(12f * a - B) + C
            }
        }
    }
}