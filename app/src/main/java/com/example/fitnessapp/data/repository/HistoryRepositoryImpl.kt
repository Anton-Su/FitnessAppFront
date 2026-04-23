package com.example.fitnessapp.data.repository

import com.example.fitnessapp.data.local.dao.HistoryDao
import com.example.fitnessapp.data.local.entity.HistoryEntity
import com.example.fitnessapp.domain.repository.HistoryRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * Реализация репозитория истории, использующая Room DAO [HistoryDao].
 *
 * Оборачивает операции DAO и предоставляет их доменному слою.
 */
class HistoryRepositoryImpl(private val dao: HistoryDao) : HistoryRepository {
    private val gson = Gson()

    /** Возвращает поток со всеми записями истории. */
    override fun getHistory(): Flow<List<HistoryEntity>> = dao.getAllHistory()

    /** Вставляет запись истории. */
    override suspend fun insert(history: HistoryEntity) = dao.insert(history)

    /** Удаляет запись по id. */
    override suspend fun deleteById(id: Long) = dao.deleteById(id)

    /** Очищает всю историю. */
    override suspend fun clearAll() = dao.clearAll()

    /**
     * Экспортирует всю историю в JSON.
     * Использует первый эмит из Flow списка записей истории.
     */
    override suspend fun exportHistoryJson(): String {
        val list = dao.getAllHistory().first()
        return gson.toJson(list)
    }
}
