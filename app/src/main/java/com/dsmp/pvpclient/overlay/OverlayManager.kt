package com.dsmp.pvpclient.overlay

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages starting and stopping the [OverlayService].
 *
 * Checks for [Settings.canDrawOverlays] before starting;
 * callers should redirect the user to grant permission if [canShow] returns false.
 */
@Singleton
class OverlayManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /** True when the app holds the SYSTEM_ALERT_WINDOW permission. */
    val canShow: Boolean
        get() = Settings.canDrawOverlays(context)

    /**
     * Starts the overlay service with the supplied display flags.
     * @return true if the service was started, false if permission is missing.
     */
    fun start(
        showFps: Boolean  = true,
        showCps: Boolean  = true,
        showPing: Boolean = false
    ): Boolean {
        if (!canShow) return false

        val intent = Intent(context, OverlayService::class.java).apply {
            putExtra(OverlayService.EXTRA_SHOW_FPS,  showFps)
            putExtra(OverlayService.EXTRA_SHOW_CPS,  showCps)
            putExtra(OverlayService.EXTRA_SHOW_PING, showPing)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
        return true
    }

    /** Stops the overlay service. */
    fun stop() {
        context.stopService(Intent(context, OverlayService::class.java))
    }

    /**
     * Convenience toggle — starts if stopped, stops if running.
     * Returns the new state (true = now running).
     */
    fun toggle(
        currentlyRunning: Boolean,
        showFps: Boolean  = true,
        showCps: Boolean  = true,
        showPing: Boolean = false
    ): Boolean {
        return if (currentlyRunning) {
            stop()
            false
        } else {
            start(showFps, showCps, showPing)
        }
    }
}
