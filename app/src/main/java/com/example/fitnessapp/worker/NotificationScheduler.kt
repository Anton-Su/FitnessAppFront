package com.example.fitnessapp.worker

import android.content.Context
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Scheduler для планирования ежедневных уведомлений в заданные времена.
 *
 * Использует PeriodicWorkRequest с repeatInterval = 24 часа и задаёт initialDelay до ближайшего
 * вхождения времени (07:30 и 18:50). Работа регистрируется как уникальная (uniqueName).
 */
object NotificationScheduler {
    private const val WORK_NAME_MORNING = "daily_notification_morning"
    private const val WORK_NAME_EVENING = "daily_notification_evening"

    fun scheduleDailyNotifications(context: Context) {
        scheduleAt(context, 7, 30, WORK_NAME_MORNING, "Доброе утро", "Пора размяться и начать день с упражнениями")
        scheduleAt(context, 18, 50, WORK_NAME_EVENING, "Вечерняя тренировка", "Время для вечерней активности")
    }

    private fun scheduleAt(context: Context, hour: Int, minute: Int, uniqueName: String, title: String, text: String) {
        val now = Calendar.getInstance()
        val next = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(now) || equals(now)) {
                add(Calendar.DATE, 1)
            }
        }

        val initialDelayMillis = next.timeInMillis - now.timeInMillis

        val input = androidx.work.Data.Builder().putString("title", title).putString("text", text).build()

        val request = androidx.work.PeriodicWorkRequestBuilder<com.example.fitnessapp.worker.NotificationWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelayMillis, TimeUnit.MILLISECONDS)
            .setInputData(input)
            .build()

        androidx.work.WorkManager.getInstance(context).enqueueUniquePeriodicWork(uniqueName, androidx.work.ExistingPeriodicWorkPolicy.REPLACE, request)
    }
}
