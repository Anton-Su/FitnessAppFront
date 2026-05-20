package com.example.fitnessapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Сущность настроек пользователя.
 */
@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey
    val id: Long = 0,

    @ColumnInfo(name = "user_id")
    val userId: Int = 0,

    @ColumnInfo(name = "age")
    val age: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "height")
    val height: Double,

    @ColumnInfo(name = "weight")
    val weight: Double,

    @ColumnInfo(name = "status_active")
    val statusActive: Boolean,

    @ColumnInfo(name = "goal")
    val goal: Int
)

