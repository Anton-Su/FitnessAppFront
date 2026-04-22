package com.example.fitnessapp.domain.usecase

import com.example.fitnessapp.data.local.entity.HistoryEntity
import com.example.fitnessapp.domain.repository.HistoryRepository

/**
 * UseCase для добавления записи истории активности пользователя.
 *
 * Инкапсулирует логику вставки [HistoryEntity] в репозиторий истории.
 * Используется из ViewModel или других компонентов для записи дневных данных (калории, шаги).
 *
 * @param repo репозиторий истории (HistoryRepository)
 */
class InsertHistoryUseCase(private val repo: HistoryRepository) {
    /**
     * Выполняет вставку записи истории.
     * @param history объект [HistoryEntity] для сохранения
     */
    suspend operator fun invoke(history: HistoryEntity) = repo.insert(history)
}
