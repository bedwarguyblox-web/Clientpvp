package com.dsmp.pvpclient.overlay

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.dsmp.pvpclient.MainActivity
import kotlinx.coroutines.*

/**
 * Foreground [Service] that draws a compact FPS / CPS overlay on top of Minecraft
 * using [WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY].
 *
 * Lifecycle:
 *   Start → [startForeground] with notification → add overlay view
 *   Stop  → remove overlay view → stop service
 */
class OverlayService : Service() {

    companion object {
        const val CHANNEL_ID    = "dsmp_overlay_channel"
        const val NOTIF_ID      = 1001
        const val ACTION_STOP   = "com.dsmp.pvpclient.STOP_OVERLAY"

        // Extras
        const val EXTRA_SHOW_FPS   = "show_fps"
        const val EXTRA_SHOW_CPS   = "show_cps"
        const val EXTRA_SHOW_PING  = "show_ping"
    }

    private lateinit var windowManager: WindowManager
    private var overlayView: View? = null

    // Simulated counters (real values would come from IPC / accessibility)
    private var fpsValue  = 60
    private var cpsValue  = 0
    private var pingValue = 0

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // Config flags passed via Intent
    private var showFps  = true
    private var showCps  = true
    private var showPing = false

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopSelf()
            return START_NOT_STICKY
        }

        showFps  = intent?.getBooleanExtra(EXTRA_SHOW_FPS, true)  ?: true
        showCps  = intent?.getBooleanExtra(EXTRA_SHOW_CPS, true)  ?: true
        showPing = intent?.getBooleanExtra(EXTRA_SHOW_PING, false) ?: false

        startForeground(NOTIF_ID, buildNotification())
        attachOverlay()
        startCounterUpdates()

        return START_STICKY
    }

    override fun onDestroy() {
        serviceScope.cancel()
        removeOverlay()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    // ── Overlay view ──────────────────────────────────────────────────────────

    private fun attachOverlay() {
        if (overlayView != null) return

        val layout = LinearLayout(this).apply {
            orientation  = LinearLayout.VERTICAL
            setPadding(12, 8, 12, 8)
            setBackgroundColor(Color.parseColor("#AA000000"))
            // Rounded corners require API 21+ view outline
        }

        val textColor = Color.parseColor("#00E676")
        val textSize  = 11f

        fun makeLabel(tag: String): TextView = TextView(this).apply {
            setTextColor(textColor)
            this.textSize   = textSize
            typeface        = android.graphics.Typeface.MONOSPACE
            isSingleLine    = true
            this.tag        = tag
        }

        if (showFps)  layout.addView(makeLabel("fps").also { it.text = "FPS: $fpsValue" })
        if (showCps)  layout.addView(makeLabel("cps").also { it.text = "CPS: $cpsValue" })
        if (showPing) layout.addView(makeLabel("ping").also { it.text = "Ping: ${pingValue}ms" })

        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 12
            y = 60
        }

        windowManager.addView(layout, params)
        overlayView = layout
    }

    private fun removeOverlay() {
        overlayView?.let {
            try { windowManager.removeView(it) } catch (_: Exception) {}
            overlayView = null
        }
    }

    // ── Counter simulation ────────────────────────────────────────────────────

    private fun startCounterUpdates() {
        serviceScope.launch {
            while (isActive) {
                delay(1000L)
                // In production: pull from shared memory / IPC with MCPE
                fpsValue  = (55..120).random()
                updateLabel("fps", "FPS: $fpsValue")
                updateLabel("ping", "Ping: ${(20..80).random()}ms")
            }
        }
    }

    private fun updateLabel(tag: String, newText: String) {
        (overlayView as? LinearLayout)?.let { layout ->
            for (i in 0 until layout.childCount) {
                val child = layout.getChildAt(i)
                if (child.tag == tag && child is TextView) {
                    child.text = newText
                }
            }
        }
    }

    // ── Notification ──────────────────────────────────────────────────────────

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Game Overlay",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description     = "DSMP PvP overlay is active"
            setShowBadge(false)
        }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun buildNotification(): Notification {
        val openIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        val stopIntent = PendingIntent.getService(
            this, 1,
            Intent(this, OverlayService::class.java).apply { action = ACTION_STOP },
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("DSMP Overlay Active")
            .setContentText("Tap to open launcher · Stop to disable")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setContentIntent(openIntent)
            .addAction(android.R.drawable.ic_delete, "Stop", stopIntent)
            .build()
    }
}
