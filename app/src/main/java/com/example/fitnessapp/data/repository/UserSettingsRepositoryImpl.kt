package com.example.fitnessapp.data.repository

import com.example.fitnessapp.data.local.dao.UserSettingsDao
import com.example.fitnessapp.data.local.entity.UserSettingsEntity
import com.example.fitnessapp.domain.repository.UserSettingsRepository
import kotlinx.coroutines.flow.Flow

/**
 * Реализация репозитория пользовательских настроек, использующая Room DAO [UserSettingsDao].
 *
 * Предоставляет поток для чтения настроек и методы для вставки/очистки.
 */
class UserSettingsRepositoryImpl(private val dao: UserSettingsDao) : UserSettingsRepository {
    /** Возвращает поток с записью настроек или null, если запись отсутствует. */
    override fun getSettings(): Flow<UserSettingsEntity?> = dao.getSettings()

    /** Вставляет или обновляет запись настроек. */
    override suspend fun upsert(settings: UserSettingsEntity) = dao.upsert(settings)

    /** Очищает таблицу настроек. */
    override suspend fun clear() = dao.clear()
}
