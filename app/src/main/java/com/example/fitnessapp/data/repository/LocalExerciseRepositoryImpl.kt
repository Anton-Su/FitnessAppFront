package com.example.fitnessapp.data.repository

import com.example.fitnessapp.data.local.dao.ExerciseDao
import com.example.fitnessapp.data.local.entity.ExerciseEntity
import com.example.fitnessapp.domain.repository.LocalExerciseRepository
import kotlinx.coroutines.flow.Flow

/**
 * Локальная реализация репозитория упражнений, использующая Room DAO [ExerciseDao].
 *
 * Предоставляет методы для получения и сохранения упражнений в локальную базу.
 */
class LocalExerciseRepositoryImpl(private val dao: ExerciseDao) : LocalExerciseRepository {
    /** Возвращает поток со всеми упражнениями в виде [ExerciseEntity]. */
    override fun getAll(): Flow<List<ExerciseEntity>> = dao.getAllExercises()

    /** Возвращает запись упражнения по id или null, если не найдено. */
    override suspend fun getById(id: Int): ExerciseEntity? = dao.getById(id)

    /** Вставляет список упражнений (REPLACE при конфликте). */
    override suspend fun insertAll(exercises: List<ExerciseEntity>) = dao.insertAll(exercises)

    /** Вставляет одно упражнение (REPLACE при конфликте). */
    override suspend fun insert(exercise: ExerciseEntity) = dao.insert(exercise)

    /** Очищает таблицу упражнений. */
    override suspend fun clear() = dao.clearAll()
}
