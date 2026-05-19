package com.example.fitnessapp.data.remote

import com.example.fitnessapp.data.remote.dto.ExerciseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ExerciseApiRetrofit {
    @GET("exercises")
    suspend fun getExercises(): List<ExerciseDto>

    @GET("exercises")
    suspend fun getExercisesByType(
        @Query("type") type: String
    ): List<ExerciseDto>

    @GET("exercises/{id}")
    suspend fun getExerciseById(
        @Path("id") id: Int
    ): ExerciseDto
}
