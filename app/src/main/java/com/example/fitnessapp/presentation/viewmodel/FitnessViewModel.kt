package com.example.fitnessapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.local.entity.UserSettingsEntity
import com.example.fitnessapp.data.preferences.SettingsDataStore
import com.example.fitnessapp.data.remote.dto.HistoryDto
import com.example.fitnessapp.domain.model.Gender
import com.example.fitnessapp.domain.repository.AuthRepository
import com.example.fitnessapp.domain.usecase.ExportHistoryUseCase
import com.example.fitnessapp.domain.usecase.GetExerciseByIdUseCase
import com.example.fitnessapp.domain.usecase.GetExercisesUseCase
import com.example.fitnessapp.domain.usecase.GetExercisesByTypeUseCase
import com.example.fitnessapp.domain.usecase.GetHistoryUseCase
import com.example.fitnessapp.domain.usecase.GetRecommendationUseCase
import com.example.fitnessapp.domain.usecase.UpsertUserSettingsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

/**
 * ViewModel верхнего уровня приложения.
 */
class FitnessViewModel(
    getExercisesUseCase: GetExercisesUseCase,
    private val getExerciseByIdUseCase: GetExerciseByIdUseCase,
    private val getExercisesByTypeUseCase: GetExercisesByTypeUseCase,
    private val getRecommendationUseCase: GetRecommendationUseCase,
    private val settingsDataStore: SettingsDataStore,
    getHistoryUseCase: GetHistoryUseCase,
    private val upsertUserSettingsUseCase: UpsertUserSettingsUseCase,
    private val authRepository: AuthRepository? = null,
    private val exportHistoryUseCase: ExportHistoryUseCase? = null
) : ViewModel() {
    companion object {
        private const val TAG = "FitnessViewModel"
    }

    //TODO
    private val _registrationState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val registrationState = _registrationState.asStateFlow()

    private val _loginState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val loginState = _loginState.asStateFlow()
    
    // Состояние сохранения профиля (для отображения пользователю результата)
    private val _saveProfileState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val saveProfileState = _saveProfileState.asStateFlow()

    val exercises = getExercisesUseCase()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    suspend fun loadExerciseById(id: Int) = getExerciseByIdUseCase(id)

    val history = getHistoryUseCase()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun upsertUserSettings(settings: UserSettingsEntity) = viewModelScope.launch(Dispatchers.IO) {
        upsertUserSettingsUseCase(settings)
    }

    val age: StateFlow<Int> = settingsDataStore.ageFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val name: StateFlow<String> = settingsDataStore.nameFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val gender: StateFlow<Gender> = settingsDataStore.genderFlow
        .map { Gender.fromCode(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, Gender.FEMALE)

    val userId: StateFlow<Int> = settingsDataStore.userIdFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

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

    val doEverytimeTask: StateFlow<Boolean> = settingsDataStore.doEverytimeTaskFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val caloriesBurned: StateFlow<Int> = settingsDataStore.caloriesFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    private val _remoteHistory = MutableStateFlow<List<HistoryDto>>(emptyList())
    val remoteHistory = _remoteHistory.asStateFlow()

    private val _recommendation = MutableStateFlow<com.example.fitnessapp.domain.model.Exercise?>(null)
    val recommendation = _recommendation.asStateFlow()

    private val _filteredExercises = MutableStateFlow<List<com.example.fitnessapp.domain.model.Exercise>>(emptyList())
    val filteredExercises = _filteredExercises.asStateFlow()

    private val _deleteState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val deleteState = _deleteState.asStateFlow()

    private val deleteUserUseCase = authRepository?.let { com.example.fitnessapp.domain.usecase.DeleteUserUseCase(it) }

    fun setAge(value: Int) = viewModelScope.launch { settingsDataStore.setAge(value) }
    fun setName(value: String) = viewModelScope.launch { settingsDataStore.setName(value) }
    fun setGender(value: Gender) = viewModelScope.launch { settingsDataStore.setGender(value.code) }
    fun setEmail(value: String) = viewModelScope.launch { settingsDataStore.setEmail(value) }
    fun setHeight(value: Double) = viewModelScope.launch { settingsDataStore.setHeight(value) }
    fun setWeight(value: Double) = viewModelScope.launch { settingsDataStore.setWeight(value) }
    fun setStatusActive(value: Boolean) = viewModelScope.launch { settingsDataStore.setStatusActive(value) }
    fun setGoal(value: Int) = viewModelScope.launch { settingsDataStore.setGoal(value) }

    fun addCalories(value: Int) = viewModelScope.launch {
        try {
            val current = settingsDataStore.caloriesFlow.first()
            settingsDataStore.setCalories(current + value)
        } catch (_: Exception) {
            // ignore
        }
    }

    fun updateUserProfile(
        name: String,
        gender: Gender,
        email: String,
        age: Int,
        height: Double,
        weight: Double,
        goal: Int
    ) {
        val repo = authRepository ?: return
        viewModelScope.launch(Dispatchers.IO) {
            _saveProfileState.value = AuthUiState.Loading
            try {
                val userId = settingsDataStore.userIdFlow.first()
                val password = settingsDataStore.passwordFlow.first()
                if (userId <= 0) {
                    Log.e(TAG, "updateUserProfile skipped: userId is not set")
                    _saveProfileState.value = AuthUiState.Error("Что-то пошло не так у сервера")
                    return@launch
                }

                repo.updateUserProfile(
                    userId = userId,
                    firstName = name,
                    email = email,
                    age = age,
                    height = height,
                    weight = weight,
                    password = password
                ).fold(
                    onSuccess = {
                        settingsDataStore.setName(name)
                        settingsDataStore.setGender(gender.code)
                        settingsDataStore.setEmail(email)
                        settingsDataStore.setAge(age)
                        settingsDataStore.setHeight(height)
                        settingsDataStore.setWeight(weight)
                        settingsDataStore.setGoal(goal)
                        upsertUserSettings(
                            UserSettingsEntity(
                                id = 0,
                                userId = userId,
                                age = age,
                                name = name,
                                gender = gender.code,
                                email = email,
                                height = height,
                                weight = weight,
                                statusActive = statusActive.value,
                                goal = goal
                            )
                        )
                        _saveProfileState.value = AuthUiState.Success("Сохранено")
                    },
                    onFailure = {
                        Log.e(TAG, "updateUserProfile failed", it)
                        _saveProfileState.value = AuthUiState.Error("Что-то пошло не так у сервера")
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "updateUserProfile failed", e)
                _saveProfileState.value = AuthUiState.Error("Что-то пошло не так у сервера")
            }
        }
    }

    fun resetSaveProfileState() {
        _saveProfileState.value = AuthUiState.Idle
    }

    fun deleteUser() {
        val repo = authRepository ?: return
        viewModelScope.launch(Dispatchers.IO) {
            _deleteState.value = AuthUiState.Loading
            try {
                val userId = settingsDataStore.userIdFlow.first()
                if (userId <= 0) {
                    _deleteState.value = AuthUiState.Error("User id not set")
                    return@launch
                }
                val res = deleteUserUseCase?.invoke(userId) ?: Result.failure(Exception("No repo"))
                if (res.isSuccess) {
                    // clear local tokens and simple settings
                    settingsDataStore.setUserId(0)
                    settingsDataStore.setName("")
                    settingsDataStore.setEmail("")
                    settingsDataStore.setPassword("")
                    settingsDataStore.setAge(18)
                    settingsDataStore.setHeight(0.0)
                    settingsDataStore.setWeight(0.0)
                    settingsDataStore.setGoal(0)
                    settingsDataStore.setStatusActive(false)
                    logout()
                    _deleteState.value = AuthUiState.Success("Аккаунт удалён")
                } else {
                    _deleteState.value = AuthUiState.Error(res.exceptionOrNull()?.message ?: "Не удалось удалить аккаунт")
                }
            } catch (e: Exception) {
                _deleteState.value = AuthUiState.Error(e.message ?: "Не удалось удалить аккаунт")
            }
        }
    }

    fun registerUser(firstName: String, gender: Gender, email: String, age: Int, password: String, height: Double, weight: Double) {
        val repo = authRepository ?: return
        viewModelScope.launch(Dispatchers.IO) {
            _registrationState.value = AuthUiState.Loading
            _registrationState.value = try {
                repo.register(firstName = firstName, email = email, age = age, password = password).fold(
                    onSuccess = {
                        settingsDataStore.setName(firstName)
                        settingsDataStore.setGender(gender.code)
                        settingsDataStore.setEmail(email)
                        settingsDataStore.setAge(age)
                        settingsDataStore.setPassword(password)
                        settingsDataStore.setHeight(height)
                        settingsDataStore.setWeight(weight)
                        settingsDataStore.setUserId(it)
                        upsertUserSettings(
                            UserSettingsEntity(
                                id = 0,
                                userId = it,
                                age = age,
                                name = firstName,
                                gender = gender.code,
                                email = email,
                                height = height,
                                weight = weight,
                                statusActive = statusActive.value,
                                goal = goal.value
                            )
                        )
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
                        settingsDataStore.setUserId(it)
                        upsertUserSettings(
                            UserSettingsEntity(
                                id = 0,
                                userId = it,
                                age = age.value,
                                name = name.value,
                                gender = gender.value.code,
                                email = email,
                                height = height.value,
                                weight = weight.value,
                                statusActive = statusActive.value,
                                goal = goal.value
                            )
                        )
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

    fun ensureDailyDoEverytimeTaskReset() {
        viewModelScope.launch(Dispatchers.IO) {
            settingsDataStore.resetDoEverytimeTaskIfNewDay()
        }
    }

    fun loadRemoteHistory(userId: Int) {
        val repo = authRepository ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val res = repo.getHistory(userId)
                _remoteHistory.value = res.getOrDefault(emptyList())
                if (res.isFailure) {
                    Log.e(TAG, "loadRemoteHistory failed", res.exceptionOrNull())
                }
            } catch (e: Exception) {
                Log.e(TAG, "loadRemoteHistory failed", e)
                _remoteHistory.value = emptyList()
            }
        }
    }

    fun markDoEverytimeTaskDone() {
        viewModelScope.launch(Dispatchers.IO) {
            settingsDataStore.setDoEverytimeTask(true)
            _recommendation.value = null
        }
    }

    fun clearRecommendation() {
        _recommendation.value = null
    }

    fun loadRecommendation(userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                settingsDataStore.resetDoEverytimeTaskIfNewDay()
                val isDoneToday = settingsDataStore.doEverytimeTaskFlow.first()
                if (isDoneToday) {
                    _recommendation.value = null
                    return@launch
                }
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

    suspend fun exportHistoryJson(): String? {
        return try {
            exportHistoryUseCase?.invoke()
        } catch (e: Exception) {
            Log.e(TAG, "exportHistoryJson failed", e)
            null
        }
    }
}
