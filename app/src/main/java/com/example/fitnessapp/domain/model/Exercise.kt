package com.example.fitnessapp.domain.model

/**
 * Доменная модель упражнения.
 *
 * @property id уникальный идентификатор упражнения
 * @property title название упражнения
 * @property description краткое описание и инструкции
 * @property videoUrl ссылка на видео с демонстрацией
 * @property type тип упражнения (например: "strength", "cardio", "flexibility")
 */
data class Exercise(
    val id: Int,
    val title: String,
    val description: String,
    val videoUrl: String,
    val type: String,
    /** Примерная энергия, сожжённая при выполнении этого упражнения (в килокалориях). По умолчанию 0.0 */
    val caloriesBurnt: Double = 0.0
)
