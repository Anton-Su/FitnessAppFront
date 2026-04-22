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
}