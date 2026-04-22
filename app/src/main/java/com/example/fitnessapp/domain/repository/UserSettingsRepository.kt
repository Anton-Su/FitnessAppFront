package com.example.fitnessapp.domain.repository

import com.example.fitnessapp.data.local.entity.UserSettingsEntity
import kotlinx.coroutines.flow.Flow

/**
 * Контракт репозитория для пользовательских настроек.
 *
 * Определяет операции чтения (Flow) и обновления настроек в локальном хранилище.
 */
interface UserSettingsRepository {
    /** Возвращает поток с текущей записью настроек или null. */
    fun getSettings(): Flow<UserSettingsEntity?>

    /** Вставляет или обновляет запись настроек. */
    suspend fun upsert(settings: UserSettingsEntity)

    /** Очищает таблицу настроек. */
    suspend fun clear()
}
