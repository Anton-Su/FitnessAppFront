package com.example.fitnessapp.data.repository

import com.example.fitnessapp.domain.model.Exercise
import com.example.fitnessapp.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Временная репозитория упражнений для примера и разработки..
 */
class ExerciseRepositoryImpl : ExerciseRepository {
    private val sample = listOf(
        Exercise(1, "Приседания", "Классические приседания", "https://example.com/video1.mp4", "Test"),
        Exercise(2, "Отжимания", "Классические отжимания", "https://example.com/video2.mp4", "Test"),
        Exercise(3, "Планка", "Удержание планки", "https://example.com/video3.mp4", "Test")
    )

    /**
     * Возвращает поток со статическим списком упражнений.
     */
    override fun getExercises(): Flow<List<Exercise>> = flow {
        emit(sample)
    }

    /**
     * Возвращает упражнение по идентификатору из тестового набора или null, если не найдено.
     * @param id идентификатор упражнения
     */
    override suspend fun getExerciseById(id: Int): Exercise? {
        return sample.firstOrNull { it.id == id }
    }
}
