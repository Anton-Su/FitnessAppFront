package com.example.fitnessapp.domain.usecase

import com.example.fitnessapp.data.local.entity.UserSettingsEntity
import com.example.fitnessapp.domain.repository.UserSettingsRepository

/**
 * UseCase для вставки или обновления пользовательских настроек (upsert) в локальный репозиторий.
 *
 * Инкапсулирует вызов репозитория и может быть вызван из ViewModel при сохранении настроек.
 */
class UpsertUserSettingsUseCase(private val repo: UserSettingsRepository) {
    /**
     * Вставляет или обновляет запись настроек.
     * @param settings сущность [UserSettingsEntity]
     */
    suspend operator fun invoke(settings: UserSettingsEntity) = repo.upsert(settings)
}
