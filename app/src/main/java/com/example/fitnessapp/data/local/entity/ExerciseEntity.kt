package com.example.fitnessapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Сущность для хранения упражнений в Room.
 */
@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "video_url")
    val videoUrl: String,
    @ColumnInfo(name = "type")
    val type: String
    ,
    /** Сохранённое (возможно рассчитанное) значение калорий для упражнения */
    @ColumnInfo(name = "calories_burnt")
    val caloriesBurnt: Double = 0.0
)

