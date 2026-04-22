package com.example.fitnessapp.domain.repository

import com.example.fitnessapp.domain.model.Exercise
import kotlinx.coroutines.flow.Flow

/**
 * Репозиторий упражнений.
 *
 * Определяет контракт взаимодействия с источником данных упражнений (локальным или удалённым).
 */
interface ExerciseRepository {
    /**
     * Возвращает поток со списком упражнений.
     */
    fun getExercises(): Flow<List<Exercise>>

    /**
     * Возвращает упражнение по идентификатору или null, если не найдено.
     */
    suspend fun getExerciseById(id: Int): Exercise?
}
