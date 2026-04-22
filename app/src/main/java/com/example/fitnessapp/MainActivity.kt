package com.example.fitnessapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.fitnessapp.data.model.AppDatabase
import com.example.fitnessapp.data.preferences.SettingsDataStore
import com.example.fitnessapp.data.repository.ExerciseRepositoryImpl
import com.example.fitnessapp.data.repository.HistoryRepositoryImpl
import com.example.fitnessapp.data.repository.LocalExerciseRepositoryImpl
import com.example.fitnessapp.data.repository.UserSettingsRepositoryImpl
import com.example.fitnessapp.domain.usecase.GetExerciseByIdUseCase
import com.example.fitnessapp.domain.usecase.GetExercisesUseCase
import com.example.fitnessapp.domain.usecase.GetHistoryUseCase
import com.example.fitnessapp.domain.usecase.InsertHistoryUseCase
import com.example.fitnessapp.domain.usecase.GetUserSettingsUseCase
import com.example.fitnessapp.domain.usecase.SyncExercisesUseCase
import com.example.fitnessapp.domain.usecase.UpsertUserSettingsUseCase
import com.example.fitnessapp.navigation.Navigation
import com.example.fitnessapp.presentation.viewmodel.FitnessViewModel
import com.example.fitnessapp.ui.theme.FitnessAppTheme
import com.example.fitnessapp.worker.NotificationScheduler

/**
 * Главная Activity приложения.
 *
 * Отвечает за создание необходимых зависимостей (репозиториев, usecase'ов, SettingsDataStore)
 * Затем запускает Compose UI и передаёт viewModel в навигацию.
 */
class MainActivity : ComponentActivity() {
    /**
     * Инициализация Activity: создаём зависимости вручную и запускаем Compose через setContent.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val settingsDataStore = SettingsDataStore(applicationContext)

        // Room database и DAO
        val db = AppDatabase.getInstance(applicationContext)
        val historyDao = db.historyDao()
        val userSettingsDao = db.userSettingsDao()

        // Репозитории
        val exerciseRepo = ExerciseRepositoryImpl()
        val historyRepo = HistoryRepositoryImpl(historyDao)
        val userSettingsRepo = UserSettingsRepositoryImpl(userSettingsDao)

        // Remote API и репозиторий
        val remoteApi = com.example.fitnessapp.data.remote.KtorRemoteApi("https://your.server.api")
        val remoteExerciseRepo = com.example.fitnessapp.data.repository.RemoteExerciseRepository(remoteApi)

        // UseCases
        val getExercisesUseCase = GetExercisesUseCase(exerciseRepo)
        val getExerciseByIdUseCase = GetExerciseByIdUseCase(exerciseRepo)
        val getHistoryUseCase = GetHistoryUseCase(historyRepo)
        val insertHistoryUseCase = InsertHistoryUseCase(historyRepo)
        val getUserSettingsUseCase = GetUserSettingsUseCase(userSettingsRepo)
        val upsertUserSettingsUseCase = UpsertUserSettingsUseCase(userSettingsRepo)

        // Sync usecase
        val localExerciseRepo = LocalExerciseRepositoryImpl(db.exerciseDao())
        val syncExercisesUseCase = SyncExercisesUseCase(remoteExerciseRepo, localExerciseRepo)

        // Создаём ViewModel напрямую (без фабрики)
        val viewModel = FitnessViewModel(
            getExercisesUseCase,
            getExerciseByIdUseCase,
            settingsDataStore,
            getHistoryUseCase,
            insertHistoryUseCase,
            getUserSettingsUseCase,
            upsertUserSettingsUseCase,
            syncExercisesUseCase
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

        // Планируем ежедневные уведомления (07:30 и 18:50)
        NotificationScheduler.scheduleDailyNotifications(this)
    }
}