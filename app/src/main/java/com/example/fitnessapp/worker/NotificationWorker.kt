package com.example.fitnessapp.worker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.ListenableWorker
import com.example.fitnessapp.data.preferences.SettingsDataStore
import com.example.fitnessapp.MainActivity
import com.example.fitnessapp.R
import kotlinx.coroutines.flow.first

/**
 * WorkManager worker, который отправляет уведомление пользователю.
 */
class NotificationWorker(
    private val ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {

    companion object {
        const val CHANNEL_ID = "daily_notifications_channel"
        const val CHANNEL_NAME = "Daily Notifications"
        const val NOTIF_ID = 3001
    }

    override suspend fun doWork(): ListenableWorker.Result {
        try {
            createNotificationChannel()

            val settings = SettingsDataStore(ctx)
            val isActive = settings.statusActiveFlow.first()
            if (!isActive) {
                return Result.success()
            }

            val canPost = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            } else true

            if (!canPost) {
                return Result.success()
            }

            val activityIntent = Intent(ctx, MainActivity::class.java)
            val pendingFlags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            val pendingIntent = PendingIntent.getActivity(ctx, 0, activityIntent, pendingFlags)

            val title = inputData.getString("title") ?: ctx.getString(R.string.app_name)
            val text = inputData.getString("text") ?: "Пора тренировка!"

            val notif: Notification = NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()

            val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.notify(NOTIF_ID + (System.currentTimeMillis() % 1000).toInt(), notif)

            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            nm.createNotificationChannel(channel)
        }
    }
}
