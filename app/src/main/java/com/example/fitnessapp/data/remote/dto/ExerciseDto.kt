package com.example.fitnessapp.data.remote.dto

import com.example.fitnessapp.domain.model.Exercise

/**
 * DTO — переносимый объект упражнения, получаемый с удалённого сервера.
 *
 * Используется в сетевом слое для парсинга ответа и последующего преобразования
 * в доменную или локальную модель.
 *
 * @property id уникальный идентификатор упражнения
 * @property title название упражнения
 * @property description текстовое описание и инструкции по выполнению
 * @property videoUrl URL-адрес видео с демонстрацией упражнения
 * @property type тип упражнения (например: "cardio", "strength", "flexibility" и т.д.)
 */
data class ExerciseDto(
    val id: Int,
    val title: String,
    val description: String,
    val videoUrl: String,
    val type: String,
    // Сервер может снабжать упражнение подсчитанными калориями — используем Double и значение по умолчанию
    val caloriesBurnt: Double = 0.0
)

fun ExerciseDto.toDomain(): Exercise = Exercise(
    id = id,
    title = title,
    description = description,
    videoUrl = videoUrl,
    type = type,
    caloriesBurnt = caloriesBurnt
)