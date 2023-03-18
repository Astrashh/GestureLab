package com.astrash.gesturelab.system

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.service.notification.NotificationListenerService
import android.util.Log
import android.view.KeyEvent
import androidx.core.app.NotificationManagerCompat
import kotlin.math.roundToInt

class MediaController(
    private val context: Context,
    private val audioManager: AudioManager,
    private val mediaSessionManager: MediaSessionManager,
) {
    fun getVolumePercent() =
        audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat() / getMaxVolume().toFloat()

    fun setVolume(percent: Float) {
        val volume = (percent * getMaxVolume()).roundToInt()
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)
    }

    private fun getMaxVolume() =
        audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

    private fun hasNotificationReadPerms(): Boolean =
        NotificationManagerCompat.getEnabledListenerPackages(context).contains(context.packageName)

    fun seek(percent: Float) {
        getMediaController()?.apply {
            metadata
                ?.getLong(MediaMetadata.METADATA_KEY_DURATION)
                ?.also { if (it != -1L) transportControls.seekTo((it.toFloat() * percent).toLong()) }
        }
    }

    fun getDurationElapsedPercent(): Float? {
        return getMediaController()?.let {
            it.metadata
                ?.getLong(MediaMetadata.METADATA_KEY_DURATION)
                ?.toFloat()
                ?.let { divisor ->
                    it.playbackState
                        ?.position
                        ?.toFloat()
                        ?.div(divisor)
                }
        }
    }

    private fun getMediaController(): MediaController? {
        return if (!hasNotificationReadPerms()) {
            Log.d(SystemActions.TAG, "Failed to read media: No notification read permission")
            null
        } else
            mediaSessionManager.getActiveSessions(
                ComponentName(
                    context,
                    NotificationListenerService::class.java
                )
            )
                .let { list ->
                    list.find { it?.playbackState?.state == PlaybackState.STATE_PLAYING }
                        ?: list.firstOrNull()
                }
                .also { it ?: Log.d(SystemActions.TAG, "Could not find media") }
                ?.also {
                    Log.d(
                        SystemActions.TAG,
                        "Found media '${it.metadata?.description}' playing from ${it.packageName}"
                    )
                }
    }

    fun openPlayingMediaApp() {
        getMediaController()
            ?.packageName
            ?.let(context.packageManager::getLaunchIntentForPackage)
            ?.also { it.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION) }
            .also { it ?: Log.d(SystemActions.TAG, "Could not create intent for package") }
            ?.also(context::startActivity)
    }

    fun togglePlayPause() {
        audioManager.dispatchMediaKeyEvent(
            KeyEvent(
                KeyEvent.ACTION_DOWN,
                KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
            )
        )
        audioManager.dispatchMediaKeyEvent(
            KeyEvent(
                KeyEvent.ACTION_UP,
                KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
            )
        )
    }

    fun pause() {
        audioManager.dispatchMediaKeyEvent(
            KeyEvent(
                KeyEvent.ACTION_DOWN,
                KeyEvent.KEYCODE_MEDIA_PAUSE
            )
        )
        audioManager.dispatchMediaKeyEvent(
            KeyEvent(
                KeyEvent.ACTION_UP,
                KeyEvent.KEYCODE_MEDIA_PAUSE
            )
        )
    }

    fun next() {
        audioManager.dispatchMediaKeyEvent(
            KeyEvent(
                KeyEvent.ACTION_DOWN,
                KeyEvent.KEYCODE_MEDIA_NEXT
            )
        )
        audioManager.dispatchMediaKeyEvent(
            KeyEvent(
                KeyEvent.ACTION_UP,
                KeyEvent.KEYCODE_MEDIA_NEXT
            )
        )
    }

    fun previous() {
        audioManager.dispatchMediaKeyEvent(
            KeyEvent(
                KeyEvent.ACTION_DOWN,
                KeyEvent.KEYCODE_MEDIA_PREVIOUS
            )
        )
        audioManager.dispatchMediaKeyEvent(
            KeyEvent(
                KeyEvent.ACTION_UP,
                KeyEvent.KEYCODE_MEDIA_PREVIOUS
            )
        )
    }
}