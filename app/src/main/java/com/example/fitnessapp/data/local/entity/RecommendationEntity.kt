package com.example.fitnessapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Локальный кэш рекомендации дня для конкретного пользователя.
 */
@Entity(tableName = "recommendations")
data class RecommendationEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: Int,
    @ColumnInfo(name = "exercise_id")
    val exerciseId: Int,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "video_url")
    val videoUrl: String,
    @ColumnInfo(name = "type")
    val type: String
)

