package com.example.fitnessapp.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

private val Context.dataStore by preferencesDataStore(name = "settings")


/**
 * Хранилище настроек пользователя на основе Preferences DataStore.
 *
 * Предоставляет потоки (Flow) для чтения значений и suspend-функции для записи.
 * Используется для хранения простых параметров профиля: возраст, имя, рост, вес,
 * Статус пользователя (активен он или нет) и цель по снижению веса.
 *
 * @property context Контекст приложения (используется для доступа к DataStore).
 */
class SettingsDataStore(private val context: Context) {
    companion object {
        /** Ключ для хранения возраста пользователя (Int). */
        val AGE = intPreferencesKey("age")
        /** Ключ для хранения имени пользователя (String). */
        val NAME = stringPreferencesKey("name")
        /** Ключ для хранения пола пользователя (String): male / female. */
        val GENDER = stringPreferencesKey("gender")
        /** Ключ для хранения идентификатора пользователя (Int). */
        val USER_ID = intPreferencesKey("user_id")
        /** Ключ для хранения фамилии пользователя (String). */
        /** Ключ для хранения email пользователя (String). */
        val EMAIL = stringPreferencesKey("email")
        /** Ключ для хранения пароля пользователя (String). */
        val PASSWORD = stringPreferencesKey("password")
        /** Ключ для хранения роста пользователя (Double).
         *  Хранит дробное значение в сантиметрах.
         */
        val HEIGHT = doublePreferencesKey("height")
        /** Ключ для хранения веса пользователя (Double).
         */
        val WEIGHT = doublePreferencesKey("weight")
        /** Ключ для хранения булевого статуса активности (true = active). */
        val STATUS_ACTIVE = booleanPreferencesKey("status_active")
        /** Ключ для хранения числовой цели (Int) — сколько кг пользователь хочет сбросить. */
        val GOAL = intPreferencesKey("goal")
        /** Ключ для хранения текущего количества шагов за день. */
        val STEPS = intPreferencesKey("steps")
        /** Ключ для хранения текущих сожжённых калорий за день. */
        val CALORIES = intPreferencesKey("calories")
        /** Флаг дневной задачи: выполнил рекомендованное упражнение сегодня. */
        val DO_EVERYTIME_TASK = booleanPreferencesKey("do_everytime_task")
        /** Дата, когда последний раз проверяли/сбрасывали DO_EVERYTIME_TASK (yyyy-MM-dd). */
        val DO_EVERYTIME_TASK_DATE = stringPreferencesKey("do_everytime_task_date")
    }

    /**
     * Поток с текущим значением возраста (Int). По умолчанию 18.
     */
    val ageFlow: Flow<Int> = context.dataStore.data
        .map { prefs: Preferences ->
            prefs[AGE] ?: 18
        }

    /**
     * Поток с текущим именем пользователя (String). По умолчанию пустая строка.
     */
    val nameFlow: Flow<String> = context.dataStore.data
        .map { prefs: Preferences ->
            prefs[NAME] ?: ""
        }

    /**
     * Поток с текущим полом пользователя (String). По умолчанию female.
     */
    val genderFlow: Flow<String> = context.dataStore.data
        .map { prefs: Preferences ->
            prefs[GENDER] ?: "female"
        }

    /**
     * Поток с идентификатором пользователя. По умолчанию 0.
     */
    val userIdFlow: Flow<Int> = context.dataStore.data
        .map { prefs: Preferences ->
            prefs[USER_ID] ?: 0
        }

    /**
     * Поток с текущим email пользователя (String). По умолчанию пустая строка.
     */
    val emailFlow: Flow<String> = context.dataStore.data
        .map { prefs: Preferences ->
            prefs[EMAIL] ?: ""
        }

    /**
     * Поток с текущим паролем пользователя (String). По умолчанию пустая строка.
     * Примечание: пароль обычно не хранится в защищённых приложениях, но может быть полезен для кеша поля формы.
     */
    val passwordFlow: Flow<String> = context.dataStore.data
        .map { prefs: Preferences ->
            prefs[PASSWORD] ?: ""
        }

    /**
     * Поток с текущим ростом пользователя (Double). По умолчанию 0.0.
     */
    val heightFlow: Flow<Double> = context.dataStore.data
        .map { prefs: Preferences ->
            prefs[HEIGHT] ?: 0.0
        }

    /**
     * Поток с текущим весом пользователя (Double). По умолчанию 0.0.
     */
    val weightFlow: Flow<Double> = context.dataStore.data
        .map { prefs: Preferences ->
            prefs[WEIGHT] ?: 0.0
        }

    /**
     * Поток с булевым статусом активности пользователя (true = active). По умолчанию false.
     */
    val statusActiveFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs: Preferences ->
            prefs[STATUS_ACTIVE] ?: false
        }

    /**
     * Поток с целевым значением снижения веса (Int) — сколько кг пользователь хочет сбросить.
     * По умолчанию 0.
     */
    val goalFlow: Flow<Int> = context.dataStore.data
        .map { prefs: Preferences ->
            prefs[GOAL] ?: 0
        }

    /**
     * Поток с текущим количеством шагов за день. По умолчанию 0.
     */
    val stepsFlow: Flow<Int> = context.dataStore.data
        .map { prefs: Preferences ->
            prefs[STEPS] ?: 0
        }

    /**
     * Поток с текущим количеством сожжённых калорий за день. По умолчанию 0.
     */
    val caloriesFlow: Flow<Int> = context.dataStore.data
        .map { prefs: Preferences ->
            prefs[CALORIES] ?: 0
        }

    /**
     * Поток флага дневной задачи с рекомендованным упражнением.
     */
    val doEverytimeTaskFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs: Preferences ->
            prefs[DO_EVERYTIME_TASK] ?: false
        }

    /**
     * Записать возраст пользователя.
     *
     * @param value значение возраста (Int).
     */
    suspend fun setAge(value: Int) {
        context.dataStore.edit { prefs: MutablePreferences ->
            prefs[AGE] = value
        }
    }

    /**
     * Записать имя пользователя.
     *
     * @param value строка с именем.
     */
    suspend fun setName(value: String) {
        context.dataStore.edit { prefs: MutablePreferences ->
            prefs[NAME] = value
        }
    }

    /**
     * Записать пол пользователя.
     */
    suspend fun setGender(value: String) {
        context.dataStore.edit { prefs: MutablePreferences ->
            prefs[GENDER] = value
        }
    }

    /**
     * Записать идентификатор пользователя.
     */
    suspend fun setUserId(value: Int) {
        context.dataStore.edit { prefs: MutablePreferences ->
            prefs[USER_ID] = value
        }
    }

    /**
     * Записать email пользователя.
     */
    suspend fun setEmail(value: String) {
        context.dataStore.edit { prefs: MutablePreferences ->
            prefs[EMAIL] = value
        }
    }

    /**
     * Записать пароль пользователя.
     */
    suspend fun setPassword(value: String) {
        context.dataStore.edit { prefs: MutablePreferences ->
            prefs[PASSWORD] = value
        }
    }

    /**
     * Записать рост пользователя.
     *
     * @param value рост в Double (например, 175.5).
     */
    suspend fun setHeight(value: Double) {
        context.dataStore.edit { prefs: MutablePreferences ->
            prefs[HEIGHT] = value
        }
    }

    /**
     * Записать вес пользователя.
     *
     * @param value вес в Double (например, 72.3).
     */
    suspend fun setWeight(value: Double) {
        context.dataStore.edit { prefs: MutablePreferences ->
            prefs[WEIGHT] = value
        }
    }

    /**
     * Установить статус активности пользователя.
     *
     * @param value true — активен, false — не активен.
     */
    suspend fun setStatusActive(value: Boolean) {
        context.dataStore.edit { prefs: MutablePreferences ->
            prefs[STATUS_ACTIVE] = value
        }
    }

    /**
     * Записать целевое количество кг для сброса веса.
     *
     * @param value целевое количество килограммов (Int).
     */
    suspend fun setGoal(value: Int) {
        context.dataStore.edit { prefs: MutablePreferences ->
            prefs[GOAL] = value
        }
    }

    /**
     * Записать текущее количество шагов за день.
     */
    suspend fun setSteps(value: Int) {
        context.dataStore.edit { prefs: MutablePreferences ->
            prefs[STEPS] = value
        }
    }

    /**
     * Записать текущее количество сожжённых калорий за день.
     */
    suspend fun setCalories(value: Int) {
        context.dataStore.edit { prefs: MutablePreferences ->
            prefs[CALORIES] = value
        }
    }

    /**
     * Отметить выполнение рекомендованного упражнения за текущий день.
     */
    suspend fun setDoEverytimeTask(value: Boolean) {
        context.dataStore.edit { prefs: MutablePreferences ->
            prefs[DO_EVERYTIME_TASK] = value
            prefs[DO_EVERYTIME_TASK_DATE] = LocalDate.now().toString()
        }
    }

    /**
     * Сбрасывает дневной флаг при смене календарного дня.
     */
    suspend fun resetDoEverytimeTaskIfNewDay() {
        val today = LocalDate.now().toString()
        context.dataStore.edit { prefs: MutablePreferences ->
            val savedDate = prefs[DO_EVERYTIME_TASK_DATE] ?: ""
            if (savedDate != today) {
                prefs[DO_EVERYTIME_TASK] = false
                prefs[DO_EVERYTIME_TASK_DATE] = today
            }
        }
    }

}