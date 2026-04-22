package com.example.fitnessapp.domain.usecase

import com.example.fitnessapp.data.local.entity.HistoryEntity
import com.example.fitnessapp.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow

/**
 * UseCase для получения списка записей истории активности пользователя.
 *
 * Инкапсулирует обращение к репозиторию истории и возвращает Flow списка [HistoryEntity].
 */
class GetHistoryUseCase(private val repo: HistoryRepository) {
    /**
     * Возвращает Flow списка записей истории.
     */
    operator fun invoke(): Flow<List<HistoryEntity>> = repo.getHistory()
}
