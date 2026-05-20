package com.example.fitnessapp.data.remote.dto

import com.example.fitnessapp.domain.model.Exercise

/**
 * Универсальный DTO для упражнений — поддерживает старый внутренний формат
 * (id/title/videoUrl) и новый серверный формат (exercise_id/name/file_path).
 * Все поля опциональны/с дефолтами, чтобы действовать как плейсхолдеры при отсутствии данных.
 */
data class ExerciseDto(
    // новый серверный формат
    val exercise_id: Long? = null,
    val name: String = "",
    val file_path: String = "",
    // старый внутренний формат
    val id: Int? = null,
    val title: String = "",
    val description: String = "",
    val videoUrl: String = "",
    val type: String = "",
    val caloriesBurnt: Double = 0.0
)

fun ExerciseDto.toDomain(): Exercise = Exercise(
    id = (exercise_id?.toInt() ?: id ?: 0),
    title = if (title.isNotBlank()) title else name,
    description = description,
    videoUrl = if (videoUrl.isNotBlank()) videoUrl else file_path,
    type = if (type.isNotBlank()) type else run {
        when {
            name.contains("cardio", ignoreCase = true) -> "cardio"
            name.contains("strength", ignoreCase = true) -> "strength"
            name.contains("flexibility", ignoreCase = true) -> "flexibility"
            else -> "other"
        }
    },
    caloriesBurnt = caloriesBurnt
)