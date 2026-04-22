package com.example.fitnessapp.domain.repository

import com.example.fitnessapp.data.local.entity.ExerciseEntity
import kotlinx.coroutines.flow.Flow

/**
 * Контракт для локального хранилища упражнений.
 *
 * Используется для абстрагирования от конкретной реализации локальной базы (Room).
 */
interface LocalExerciseRepository {
    /** Возвращает поток всех локальных упражнений. */
    fun getAll(): Flow<List<ExerciseEntity>>

    /** Возвращает упражнение по id или null. */
    suspend fun getById(id: Int): ExerciseEntity?

    /** Вставляет или обновляет список упражнений локально. */
    suspend fun insertAll(exercises: List<ExerciseEntity>)

    /** Вставляет или обновляет одно упражнение локально. */
    suspend fun insert(exercise: ExerciseEntity)

    /** Очищает локальную таблицу упражнений. */
    suspend fun clear()
}
