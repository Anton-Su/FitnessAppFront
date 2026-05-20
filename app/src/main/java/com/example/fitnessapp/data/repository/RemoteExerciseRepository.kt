package com.example.fitnessapp.data.repository

import com.example.fitnessapp.data.remote.ExerciseApiRetrofit
import com.example.fitnessapp.data.remote.dto.ExerciseDto
import com.example.fitnessapp.data.remote.dto.toDomain
import com.example.fitnessapp.domain.model.Exercise

/**
 * Репозиторий для получения упражнений с удалённого сервера через [ExerciseApiRetrofit].
 *
 * Возвращает список доменных моделей [Exercise], применяя маппинг из DTO.
 */
class RemoteExerciseRepository(private val api: ExerciseApiRetrofit) {
    /**
     * Загружает список упражнений с сервера и возвращает их в виде доменных моделей.
     */
    suspend fun fetchExercises(): List<Exercise> = api.getExercises().map { it.toDomain() }
}
