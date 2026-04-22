package com.example.fitnessapp.domain.usecase

import com.example.fitnessapp.domain.model.Exercise
import com.example.fitnessapp.domain.repository.ExerciseRepository

/**
 * UseCase для получения одного упражнения по идентификатору.
 *
 * Возвращает объект [Exercise] или null, если упражнение не найдено.
 */
class GetExerciseByIdUseCase(private val repo: ExerciseRepository) {
    suspend operator fun invoke(id: Int): Exercise? = repo.getExerciseById(id)
}
