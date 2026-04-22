package com.example.fitnessapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.local.entity.HistoryEntity
import com.example.fitnessapp.data.local.entity.UserSettingsEntity
import com.example.fitnessapp.data.preferences.SettingsDataStore
import com.example.fitnessapp.domain.usecase.GetExerciseByIdUseCase
import com.example.fitnessapp.domain.usecase.GetExercisesUseCase
import com.example.fitnessapp.domain.usecase.GetHistoryUseCase
import com.example.fitnessapp.domain.usecase.InsertHistoryUseCase
import com.example.fitnessapp.domain.usecase.GetUserSettingsUseCase
import com.example.fitnessapp.domain.usecase.UpsertUserSettingsUseCase
import com.example.fitnessapp.domain.usecase.SyncExercisesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel верхнего уровня приложения.
 *
 * Содержит бизнес-логику и предоставляет потоки/методы для работы с данными приложения.
 * Отвечает за получение списка упражнений, историю активности, пользовательские настройки
 * и за запись настроек в Preferences DataStore.
 *
 * Все операции чтения представлены в виде Flow/StateFlow — их удобно собирать в Compose
 * через collectAsState(). Все операции записи выполняются в [viewModelScope].
 *
 * Параметры конструктора:
 * @param getExercisesUseCase usecase для получения списка упражнений
 * @param getExerciseByIdUseCase usecase для получения упражнения по id
 * @param settingsDataStore источник настроек (DataStore Preferences)
 * @param getHistoryUseCase usecase для получения истории (Room)
 * @param insertHistoryUseCase usecase для вставки записи истории (Room)
 * @param getUserSettingsUseCase usecase для получения настроек из Room
 * @param upsertUserSettingsUseCase usecase для записи/обновления настроек в Room
 * @param syncExercisesUseCase usecase для синхронизации упражнений с сервером
 */
class FitnessViewModel(
    private val getExercisesUseCase: GetExercisesUseCase,
    private val getExerciseByIdUseCase: GetExerciseByIdUseCase,
    private val settingsDataStore: SettingsDataStore,
    private val getHistoryUseCase: GetHistoryUseCase,
    private val insertHistoryUseCase: InsertHistoryUseCase,
    private val getUserSettingsUseCase: GetUserSettingsUseCase,
    private val upsertUserSettingsUseCase: UpsertUserSettingsUseCase,
    private val syncExercisesUseCase: SyncExercisesUseCase? = null
) : ViewModel() {

    /**
     * Flow списка упражнений.
     * Значение: List<Exercise> — список доменных моделей упражнений.
     */
    val exercises = getExercisesUseCase()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    /**
     * Синхронный метод для загрузки подробностей упражнения по id.
     * Возвращает объект Exercise или null если не найден.
     * Пример использования (в coroutine):
     * val exercise = viewModel.loadExerciseById(1)
     */
    suspend fun loadExerciseById(id: Int) = getExerciseByIdUseCase(id)

    // ---- History (Room) ----

    /**
     * StateFlow списка записей истории активности пользователя.
     * Каждый элемент — [HistoryEntity] с полями: id, date (ISO yyyy-MM-dd), calories, steps.
     * Значение по умолчанию — пустой список.
     */
    val history = getHistoryUseCase()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    /**
     * Вставить запись истории в базу.
     * Выполняется в IO диспетчере.
     * @param historyEntity объект истории для сохранения
     */
    fun insertHistory(historyEntity: HistoryEntity) = viewModelScope.launch(Dispatchers.IO) {
        insertHistoryUseCase(historyEntity)
    }

    // ---- User settings (Room) ----

    /**
     * StateFlow опциональной сущности пользовательских настроек, хранящихся в Room.
     * Может быть null, если запись ещё не создана.
     */
    val userSettings = getUserSettingsUseCase()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    /**
     * Вставить или обновить запись пользовательских настроек в Room (upsert).
     * Выполняется в IO диспетчере.
     */
    fun upsertUserSettings(settings: UserSettingsEntity) = viewModelScope.launch(Dispatchers.IO) {
        upsertUserSettingsUseCase(settings)
    }

    // ---- SettingsDataStore (Preferences) ----

    /**
     * Возраст пользователя (StateFlow<Int>), читается из Preferences DataStore.
     * Значение по умолчанию — 0.
     */
    val age: StateFlow<Int> = settingsDataStore.ageFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    /**
     * Имя пользователя (StateFlow<String>), читается из Preferences DataStore.
     * Значение по умолчанию — пустая строка.
     */
    val name: StateFlow<String> = settingsDataStore.nameFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    /**
     * Рост пользователя (StateFlow<Double>), читается из Preferences DataStore.
     * Значение по умолчанию — 0.0.
     */
    val height: StateFlow<Double> = settingsDataStore.heightFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    /**
     * Вес пользователя (StateFlow<Double>), читается из Preferences DataStore.
     * Значение по умолчанию — 0.0.
     */
    val weight: StateFlow<Double> = settingsDataStore.weightFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    /**
     * Булевый статус активности пользователя (StateFlow<Boolean>), читается из DataStore.
     * true — пользователь активен, false — не активен. По умолчанию false.
     */
    val statusActive: StateFlow<Boolean> = settingsDataStore.statusActiveFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    /**
     * Целевое количество кг для сброса веса (StateFlow<Int>), читается из DataStore.
     */
    val goal: StateFlow<Int> = settingsDataStore.goalFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    // Методы записи настроек в DataStore

    /**
     * Записать возраст в Preferences DataStore.
     * @param value возраст (Int)
     */
    fun setAge(value: Int) = viewModelScope.launch { settingsDataStore.setAge(value) }

    /**
     * Записать имя в Preferences DataStore.
     * @param value имя (String)
     */
    fun setName(value: String) = viewModelScope.launch { settingsDataStore.setName(value) }

    /**
     * Записать рост в Preferences DataStore.
     * @param value рост (Double)
     */
    fun setHeight(value: Double) = viewModelScope.launch { settingsDataStore.setHeight(value) }

    /**
     * Записать вес в Preferences DataStore.
     * @param value вес (Double)
     */
    fun setWeight(value: Double) = viewModelScope.launch { settingsDataStore.setWeight(value) }

    /**
     * Записать булевый статус активности в Preferences DataStore.
     * @param value true — активен, false — не активен
     */
    fun setStatusActive(value: Boolean) = viewModelScope.launch { settingsDataStore.setStatusActive(value) }

    /**
     * Записать целевое значение снижения веса в Preferences DataStore.
     * @param value целевое число килограммов (Int)
     */
    fun setGoal(value: Int) = viewModelScope.launch { settingsDataStore.setGoal(value) }

    /**
     * Синхронизация упражнений с сервером (если usecase передан).
     */
    fun syncExercises() {
        syncExercisesUseCase ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                syncExercisesUseCase()
            } catch (_: Exception) {
                // ignore for now or handle
            }
        }
    }
}
