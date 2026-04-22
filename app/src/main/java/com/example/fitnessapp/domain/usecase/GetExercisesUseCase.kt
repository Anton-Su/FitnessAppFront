package com.example.fitnessapp.domain.usecase

import com.example.fitnessapp.domain.model.Exercise
import com.example.fitnessapp.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.Flow

/**
 * UseCase для получения списка упражнений из репозитория.
 *
 * Инкапсулирует вызов репозитория и возвращает Flow списка доменных моделей [Exercise].
 */
class GetExercisesUseCase(private val repo: ExerciseRepository) {
    operator fun invoke(): Flow<List<Exercise>> = repo.getExercises()
}
