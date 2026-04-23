package com.example.fitnessapp.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.example.fitnessapp.MainActivity
import com.example.fitnessapp.R
import java.util.Locale

/**
 * Foreground service, считающий секунды в активном режиме (с возможностью паузы).
 *
 * Поведение:
 * - Сервис реагирует на интенты с action: ACTION_START, ACTION_PAUSE, ACTION_RESUME, ACTION_STOP.
 * - Ведёт внутренний счётчик секунд (Int) и раз в секунду увеличивает его, пока не в паузе.
 * - При каждом тике отправляет Broadcast с action ACTION_TICK и extra EXTRA_SECONDS.
 * - Обновляет foreground notification, чтобы пользователь видел прошедшее время.
 */
class SecondsCounterService : Service() {
    private val CHANNEL_ID = "seconds_counter_channel"
    private val NOTIF_ID = 2001

    private val handler = Handler(Looper.getMainLooper())
    private var seconds = 0
    private var isRunning = false

    private val tickRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                seconds += 1
                // Broadcast update
                val tick = Intent(ACTION_TICK).apply { putExtra(EXTRA_SECONDS, seconds) }
                sendBroadcast(tick)

                // update notification
                val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                nm.notify(NOTIF_ID, buildNotification(seconds))
            }
            handler.postDelayed(this, 1000L)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createChannel()
        startForeground(NOTIF_ID, buildNotification(seconds))
        handler.post(tickRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startCounting()
            ACTION_PAUSE -> pauseCounting()
            ACTION_RESUME -> resumeCounting()
            ACTION_STOP -> stopSelf()
        }
        return START_STICKY
    }

    private fun startCounting() {
        seconds = 0
        isRunning = true
    }

    private fun pauseCounting() {
        isRunning = false
    }

    private fun resumeCounting() {
        isRunning = true
    }

    private fun buildNotification(seconds: Int): Notification {
        val activityIntent = Intent(this, MainActivity::class.java)
        val pendingFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT else PendingIntent.FLAG_UPDATE_CURRENT
        val pendingIntent = PendingIntent.getActivity(
            this, 0, activityIntent, pendingFlags
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Счётчик секунд")
            .setContentText("Время: ${formatSeconds(seconds)}")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun formatSeconds(sec: Int): String {
        val h = sec / 3600
        val m = (sec % 3600) / 60
        val s = sec % 60
        return if (h > 0) String.format(Locale.getDefault(), "%d:%02d:%02d", h, m, s) else String.format(Locale.getDefault(), "%02d:%02d", m, s)
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(CHANNEL_ID, "Seconds Counter", NotificationManager.IMPORTANCE_LOW)
            nm.createNotificationChannel(channel)
        }
    }

    companion object {
        const val ACTION_START = "com.example.fitnessapp.action.START_SECONDS"
        const val ACTION_PAUSE = "com.example.fitnessapp.action.PAUSE_SECONDS"
        const val ACTION_RESUME = "com.example.fitnessapp.action.RESUME_SECONDS"
        const val ACTION_STOP = "com.example.fitnessapp.action.STOP_SECONDS"

        const val ACTION_TICK = "com.example.fitnessapp.action.SECONDS_TICK"
        const val EXTRA_SECONDS = "com.example.fitnessapp.extra.SECONDS"
    }
}
