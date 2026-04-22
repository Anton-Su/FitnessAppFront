package com.example.fitnessapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Сущность истории активности пользователя.
 * Хранит дату, сожжённые калории и количество шагов за день.
 */
@Entity(tableName = "user_history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** Дата записи в ISO (yyyy-mm-dd) */
    @ColumnInfo(name = "date")
    val date: String,

    /** Сожжённые калории за день */
    @ColumnInfo(name = "calories")
    val calories: Int,

    /** Количество шагов за день */
    @ColumnInfo(name = "steps")
    val steps: Int
)

