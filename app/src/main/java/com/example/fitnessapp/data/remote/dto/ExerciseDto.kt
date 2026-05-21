package com.example.fitnessapp.data.remote.dto

import com.example.fitnessapp.domain.model.Exercise

/**
 * Универсальный DTO для упражнений
 */
data class ExerciseDto(
    // новый серверный формат
    val exercise_id: Int,
    val name: String = "",
    val description: String = "",
    val file_path: String = "",
    val type: String = "",
    val calories_burnt: Double
)

fun ExerciseDto.toDomain(): Exercise = Exercise(
    id = exercise_id,
    name = name,
    description = description,
    videoUrl = file_path,
    type = type.ifBlank { "other" },
    caloriesBurnt = calories_burnt
)