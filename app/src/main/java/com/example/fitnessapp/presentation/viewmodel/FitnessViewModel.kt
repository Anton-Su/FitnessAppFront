package com.example.fitnessapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.local.entity.HistoryEntity
import com.example.fitnessapp.data.local.entity.UserSettingsEntity
import com.example.fitnessapp.data.preferences.SettingsDataStore
import com.example.fitnessapp.domain.repository.AuthRepository
import com.example.fitnessapp.domain.usecase.ExportHistoryUseCase
import com.example.fitnessapp.domain.usecase.GetExerciseByIdUseCase
import com.example.fitnessapp.domain.usecase.GetExercisesUseCase
import com.example.fitnessapp.domain.usecase.GetExercisesByTypeUseCase
import com.example.fitnessapp.domain.usecase.GetHistoryUseCase
import com.example.fitnessapp.domain.usecase.GetRecommendationUseCase
import com.example.fitnessapp.domain.usecase.GetUserSettingsUseCase
import com.example.fitnessapp.domain.usecase.InsertHistoryUseCase
import com.example.fitnessapp.domain.usecase.SyncExercisesUseCase
import com.example.fitnessapp.domain.usecase.UpsertUserSettingsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

/**
 * ViewModel верхнего уровня приложения.
 */
class FitnessViewModel(
    private val getExercisesUseCase: GetExercisesUseCase,
    private val getExerciseByIdUseCase: GetExerciseByIdUseCase,
    private val getExercisesByTypeUseCase: GetExercisesByTypeUseCase,
    private val getRecommendationUseCase: GetRecommendationUseCase,
    private val settingsDataStore: SettingsDataStore,
    private val getHistoryUseCase: GetHistoryUseCase,
    private val insertHistoryUseCase: InsertHistoryUseCase,
    private val getUserSettingsUseCase: GetUserSettingsUseCase,
    private val upsertUserSettingsUseCase: UpsertUserSettingsUseCase,
    private val syncExercisesUseCase: SyncExercisesUseCase? = null,
    private val authRepository: AuthRepository? = null,
    private val exportHistoryUseCase: ExportHistoryUseCase? = null
) : ViewModel() {
    companion object {
        private const val TAG = "FitnessViewModel"
    }

    private val _registrationState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val registrationState = _registrationState.asStateFlow()

    private val _loginState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val loginState = _loginState.asStateFlow()

    val exercises = getExercisesUseCase()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    suspend fun loadExerciseById(id: Int) = getExerciseByIdUseCase(id)

    val history = getHistoryUseCase()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun insertHistory(historyEntity: HistoryEntity) = viewModelScope.launch(Dispatchers.IO) {
        insertHistoryUseCase(historyEntity)
    }

    val userSettings = getUserSettingsUseCase()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun upsertUserSettings(settings: UserSettingsEntity) = viewModelScope.launch(Dispatchers.IO) {
        upsertUserSettingsUseCase(settings)
    }

    val age: StateFlow<Int> = settingsDataStore.ageFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val name: StateFlow<String> = settingsDataStore.nameFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val email: StateFlow<String> = settingsDataStore.emailFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val height: StateFlow<Double> = settingsDataStore.heightFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    val weight: StateFlow<Double> = settingsDataStore.weightFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    val statusActive: StateFlow<Boolean> = settingsDataStore.statusActiveFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val goal: StateFlow<Int> = settingsDataStore.goalFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val stepsToday: StateFlow<Int> = settingsDataStore.stepsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    private val _recommendation = MutableStateFlow<com.example.fitnessapp.domain.model.Exercise?>(null)
    val recommendation = _recommendation.asStateFlow()

    private val _filteredExercises = MutableStateFlow<List<com.example.fitnessapp.domain.model.Exercise>>(emptyList())
    val filteredExercises = _filteredExercises.asStateFlow()

    fun setAge(value: Int) = viewModelScope.launch { settingsDataStore.setAge(value) }
    fun setName(value: String) = viewModelScope.launch { settingsDataStore.setName(value) }
    fun setEmail(value: String) = viewModelScope.launch { settingsDataStore.setEmail(value) }
    fun setHeight(value: Double) = viewModelScope.launch { settingsDataStore.setHeight(value) }
    fun setWeight(value: Double) = viewModelScope.launch { settingsDataStore.setWeight(value) }
    fun setStatusActive(value: Boolean) = viewModelScope.launch { settingsDataStore.setStatusActive(value) }
    fun setGoal(value: Int) = viewModelScope.launch { settingsDataStore.setGoal(value) }
    fun setSteps(value: Int) = viewModelScope.launch { settingsDataStore.setSteps(value) }

    fun updateUserProfile(
        name: String,
        email: String,
        age: Int,
        height: Double,
        weight: Double,
        goal: Int
    ) {
        val repo = authRepository ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Здесь можно добавить логику отправки на сервер если нужен PUT запрос
                // Пока просто логируем
                Log.d(TAG, "updateUserProfile: name=$name, email=$email, age=$age, height=$height, weight=$weight, goal=$goal")
            } catch (e: Exception) {
                Log.e(TAG, "updateUserProfile failed", e)
            }
        }
    }

    fun syncExercises() {
        syncExercisesUseCase ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                syncExercisesUseCase()
            } catch (e: Exception) {
                Log.e(TAG, "syncExercises failed", e)
            }
        }
    }

    fun registerUser(firstName: String, email: String, age: Int, password: String, height: Double, weight: Double) {
        val repo = authRepository ?: return
        viewModelScope.launch(Dispatchers.IO) {
            _registrationState.value = AuthUiState.Loading
            _registrationState.value = try {
                repo.register(firstName = firstName, email = email, age = age, password = password).fold(
                    onSuccess = {
                        settingsDataStore.setName(firstName)
                        settingsDataStore.setEmail(email)
                        settingsDataStore.setAge(age)
                        settingsDataStore.setPassword(password)
                        settingsDataStore.setHeight(height)
                        settingsDataStore.setWeight(weight)
                        AuthUiState.Success("Регистрация завершена")
                    },
                    onFailure = { AuthUiState.Error(it.message ?: "Попробуйте снова") }
                )
            } catch (e: Exception) {
                Log.e(TAG, "registerUser failed", e)
                AuthUiState.Error(e.message ?: "Попробуйте снова")
            }
        }
    }

    fun login(email: String, password: String) {
        val repo = authRepository ?: return
        viewModelScope.launch(Dispatchers.IO) {
            _loginState.value = AuthUiState.Loading
            _loginState.value = try {
                repo.login(email = email, password = password).fold(
                    onSuccess = {
                        settingsDataStore.setEmail(email)
                        settingsDataStore.setPassword(password)
                        AuthUiState.Success("Авторизация успешна")
                    },
                    onFailure = { AuthUiState.Error(it.message ?: "Не удалось авторизоваться") }
                )
            } catch (e: Exception) {
                Log.e(TAG, "login failed", e)
                AuthUiState.Error(e.message ?: "Не удалось авторизоваться")
            }
        }
    }

    fun resetRegistrationState() {
        _registrationState.value = AuthUiState.Idle
    }

    fun resetLoginState() {
        _loginState.value = AuthUiState.Idle
    }

    fun loadRecommendation(userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val recommendation = getRecommendationUseCase(userId)
                _recommendation.value = recommendation
            } catch (e: Exception) {
                Log.e(TAG, "loadRecommendation failed", e)
                _recommendation.value = null
            }
        }
    }

    fun filterExercisesByType(type: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val filtered = getExercisesByTypeUseCase(type)
                _filteredExercises.value = filtered
            } catch (e: Exception) {
                Log.e(TAG, "filterExercisesByType failed", e)
                _filteredExercises.value = emptyList()
            }
        }
    }

    fun logout() {
        val repo = authRepository ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repo.logout()
            _loginState.value = AuthUiState.Idle
            _registrationState.value = AuthUiState.Idle
        }
    }

    suspend fun exportHistoryJson(): String? = exportHistoryUseCase?.invoke()

    suspend fun exportHistoryToFile(filePath: String): Boolean {
        val json = exportHistoryUseCase?.invoke() ?: return false
        return try {
            val file = File(filePath)
            file.parentFile?.mkdirs()
            file.writeText(json)
            true
        } catch (e: Exception) {
            Log.e(TAG, "exportHistoryToFile failed for path=$filePath", e)
            false
        }
    }
}
