package com.example.fitnessapp.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.fitnessapp.data.preferences.SettingsDataStore
import com.example.fitnessapp.data.preferences.TokenManager
import com.example.fitnessapp.data.remote.RetrofitClient
import com.example.fitnessapp.data.remote.dto.ActivityRequest
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import java.io.IOException
import kotlin.math.roundToInt

class CaloriesUploadWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    companion object {
        private const val TAG = "CaloriesUploadWorker"
    }

    override suspend fun doWork(): Result {
        val settings = SettingsDataStore(applicationContext)
        val tokenManager = TokenManager(applicationContext)
        RetrofitClient.init(applicationContext)

        return try {
            tokenManager.loadTokens()
            val token = tokenManager.getAccessToken().orEmpty().trim()
            if (token.isBlank()) {
                return Result.success()
            }

            val steps = settings.stepsFlow.first().coerceAtLeast(0)
            val calories = maxOf(0, (steps * 0.04).roundToInt())

            // send as activity; server will associate with authenticated user
            RetrofitClient.authApi.createActivity(
                request = ActivityRequest(
                    activity_date = java.time.LocalDate.now().toString(),
                    steps = steps,
                    burnt = calories,
                    goal_achieved = false
                )
            )

            settings.setSteps(0)

            Result.success()
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error while uploading calories: ${e.code()}", e)
            Result.retry()
        } catch (e: IOException) {
            Log.e(TAG, "Network error while uploading calories", e)
            Result.retry()
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error while uploading calories", e)
            Result.retry()
        } finally {
            CaloriesUploadScheduler.scheduleNext(applicationContext)
        }
    }
}

