package com.example.fitnessapp.domain.usecase

import com.example.fitnessapp.domain.model.Exercise
import com.example.fitnessapp.domain.repository.ExerciseRepository

/**
 * UseCase для получения упражнений отфильтрованных по типу.
 */
class GetExercisesByTypeUseCase(
    private val repository: ExerciseRepository
) {
    suspend operator fun invoke(type: String): List<Exercise> {
        return repository.getExercisesByType(type)
    }
}

