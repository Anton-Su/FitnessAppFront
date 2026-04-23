package com.example.fitnessapp.domain.repository

import com.example.fitnessapp.data.local.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Контракт репозитория истории активности пользователя.
 *
 * Определяет операции чтения и записи для сущности [HistoryEntity].
 */
interface HistoryRepository {
    /** Возвращает поток со списком записей истории (дата/калории/шаги). */
    fun getHistory(): Flow<List<HistoryEntity>>

    /** Вставляет запись истории. */
    suspend fun insert(history: HistoryEntity)

    /** Удаляет запись истории по id. */
    suspend fun deleteById(id: Long)

    /** Очищает всю историю. */
    suspend fun clearAll()

    /**
     * Экспортирует всю историю в JSON строку.
     * @return JSON-представление списка [HistoryEntity]
     */
    suspend fun exportHistoryJson(): String
}
