package com.example.fitnessapp.data.repository

import com.example.fitnessapp.data.remote.ExerciseApiRetrofit
import com.example.fitnessapp.data.remote.dto.ExerciseDto

/**
 * Репозиторий для получения упражнений с удалённого сервера через [ExerciseApiRetrofit].
 *
 * Содержит тонкую обёртку над API, чтобы абстрагировать сетевые вызовы от бизнес-логики.
 */
class RemoteExerciseRepository(private val api: ExerciseApiRetrofit) {
    /**
     * Загружает список упражнений с сервера и возвращает их в виде DTO списка.
     * @return List<ExerciseDto> список полученных DTO
     */
    suspend fun fetchExercises(): List<ExerciseDto> = api.getExercises()
}
