package com.example.fitnessapp.domain.usecase

import com.example.fitnessapp.data.local.entity.UserSettingsEntity
import com.example.fitnessapp.domain.repository.UserSettingsRepository
import kotlinx.coroutines.flow.Flow

/**
 * UseCase для получения пользовательских настроек из локального репозитория.
 *
 * Возвращает Flow с опциональной сущностью [UserSettingsEntity].
 */
class GetUserSettingsUseCase(private val repo: UserSettingsRepository) {
    operator fun invoke(): Flow<UserSettingsEntity?> = repo.getSettings()
}
