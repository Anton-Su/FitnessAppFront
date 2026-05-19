package com.example.fitnessapp.domain.usecase

import com.example.fitnessapp.domain.model.Exercise
import com.example.fitnessapp.domain.repository.ExerciseRepository

/**
 * UseCase для получения рекомендуемого упражнения для пользователя.
 */
class GetRecommendationUseCase(
    private val repository: ExerciseRepository
) {
    suspend operator fun invoke(userId: Int): Exercise? {
        return repository.getRecommendation(userId)
    }
}

