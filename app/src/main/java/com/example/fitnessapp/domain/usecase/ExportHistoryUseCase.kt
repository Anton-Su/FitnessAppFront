package com.example.fitnessapp.domain.usecase

import com.example.fitnessapp.domain.repository.HistoryRepository

/**
 * UseCase для экспорта истории активности пользователя в JSON строку.
 *
 * Использует [HistoryRepository] как источник данных.
 */
class ExportHistoryUseCase(private val repo: HistoryRepository) {
    /**
     * Выполняет экспорт и возвращает JSON-представление списка записей истории.
     * Бросает исключения в случае ошибок сериализации/чтения.
     */
    suspend operator fun invoke(): String = repo.exportHistoryJson()
}

