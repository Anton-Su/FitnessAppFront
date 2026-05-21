package com.example.fitnessapp.data.local.mapper

import com.example.fitnessapp.data.local.entity.ExerciseEntity
import com.example.fitnessapp.data.local.entity.RecommendationEntity
import com.example.fitnessapp.domain.model.Exercise

fun ExerciseEntity.toDomain(): Exercise = Exercise(
    id = id,
    name = name,
    description = description,
    videoUrl = videoUrl,
    type = type
)

fun RecommendationEntity.toDomain(): Exercise = Exercise(
    id = exerciseId,
    name = name,
    description = description,
    videoUrl = videoUrl,
    type = type
)

fun Exercise.toEntity(): ExerciseEntity = ExerciseEntity(
    id = id,
    name = name,
    description = description,
    videoUrl = videoUrl,
    type = type
)

fun Exercise.toRecommendationEntity(userId: Int): RecommendationEntity = RecommendationEntity(
    userId = userId,
    exerciseId = id,
    name = name,
    description = description,
    videoUrl = videoUrl,
    type = type
)

