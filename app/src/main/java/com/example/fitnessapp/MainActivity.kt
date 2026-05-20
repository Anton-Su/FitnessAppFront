package com.example.fitnessapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.Modifier
import com.example.fitnessapp.data.model.AppDatabase
import com.example.fitnessapp.data.preferences.SettingsDataStore
import com.example.fitnessapp.data.preferences.TokenManager
import com.example.fitnessapp.data.remote.RetrofitClient
import com.example.fitnessapp.data.repository.AuthRepositoryImpl
import com.example.fitnessapp.data.repository.ExerciseRepositoryImpl
import com.example.fitnessapp.data.repository.HistoryRepositoryImpl
import com.example.fitnessapp.data.repository.LocalExerciseRepositoryImpl
import com.example.fitnessapp.data.repository.UserSettingsRepositoryImpl
import com.example.fitnessapp.domain.usecase.GetExerciseByIdUseCase
import com.example.fitnessapp.domain.usecase.GetExercisesUseCase
import com.example.fitnessapp.domain.usecase.GetExercisesByTypeUseCase
import com.example.fitnessapp.domain.usecase.GetRecommendationUseCase
import com.example.fitnessapp.domain.usecase.GetHistoryUseCase
import com.example.fitnessapp.domain.usecase.UpsertUserSettingsUseCase
import com.example.fitnessapp.navigation.Navigation
import com.example.fitnessapp.presentation.viewmodel.FitnessViewModel
import com.example.fitnessapp.ui.theme.FitnessAppTheme
import com.example.fitnessapp.service.StepCounterService
import com.example.fitnessapp.worker.NotificationScheduler
import com.example.fitnessapp.worker.CaloriesUploadScheduler

/**
 * Главная Activity приложения.
 */
class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startStepCounterService()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        RetrofitClient.init(applicationContext)

        val settingsDataStore = SettingsDataStore(applicationContext)
        val tokenManager = TokenManager(applicationContext)

        val db = AppDatabase.getInstance(applicationContext)
        val historyDao = db.historyDao()
        val userSettingsDao = db.userSettingsDao()
        val recommendationDao = db.recommendationDao()

        val localExerciseRepo = LocalExerciseRepositoryImpl(db.exerciseDao())
        val exerciseRepo = ExerciseRepositoryImpl(RetrofitClient.exerciseApi, localExerciseRepo, recommendationDao)
        val historyRepo = HistoryRepositoryImpl(historyDao)
        val userSettingsRepo = UserSettingsRepositoryImpl(userSettingsDao)
        val authRepository = AuthRepositoryImpl(RetrofitClient.authApi, tokenManager)

        val getExercisesUseCase = GetExercisesUseCase(exerciseRepo)
        val getExerciseByIdUseCase = GetExerciseByIdUseCase(exerciseRepo)
        val getExercisesByTypeUseCase = GetExercisesByTypeUseCase(exerciseRepo)
        val getRecommendationUseCase = GetRecommendationUseCase(exerciseRepo)
        val getHistoryUseCase = GetHistoryUseCase(historyRepo)
        val upsertUserSettingsUseCase = UpsertUserSettingsUseCase(userSettingsRepo)

        val viewModel = FitnessViewModel(
            getExercisesUseCase,
            getExerciseByIdUseCase,
            getExercisesByTypeUseCase,
            getRecommendationUseCase,
            settingsDataStore,
            getHistoryUseCase,
            upsertUserSettingsUseCase,
            authRepository
        )

        setContent {
            FitnessAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        Navigation(viewModel = viewModel)
                    }
                }
            }
        }

        requestActivityRecognitionPermissionAndStartService()
        CaloriesUploadScheduler.scheduleNext(this)
        NotificationScheduler.scheduleDailyNotifications(this)
    }

    private fun requestActivityRecognitionPermissionAndStartService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACTIVITY_RECOGNITION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                startStepCounterService()
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.ACTIVITY_RECOGNITION)
            }
        } else {
            startStepCounterService()
        }
    }

    private fun startStepCounterService() {
        ContextCompat.startForegroundService(this, Intent(this, StepCounterService::class.java))
    }
}