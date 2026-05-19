package com.example.fitnessapp.worker

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId

object CaloriesUploadScheduler {
    private const val WORK_NAME = "midnight_calories_upload"

    fun scheduleNext(context: Context) {
        val now = LocalDateTime.now(ZoneId.systemDefault())
        val nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay()
        val delay = Duration.between(now, nextMidnight).toMillis().coerceAtLeast(0)

        val request = OneTimeWorkRequestBuilder<CaloriesUploadWorker>()
            .setInitialDelay(delay, java.util.concurrent.TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.REPLACE, request)
    }
}
