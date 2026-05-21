package com.example.fitnessapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fitnessapp.data.local.entity.ExerciseEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO для работы с локальной таблицей упражнений (`exercises`).
 *
 * Предоставляет методы для получения всех упражнений в виде [Flow],
 * получения конкретного упражнения по id, вставки/обновления записей и очистки таблицы.
 */
@Dao
interface ExerciseDao {
    /**
     * Возвращает поток со списком всех упражнений, отсортированных по названию (ASC).
     */
    @Query("SELECT * FROM exercises ORDER BY name ASC")
    fun getAllExercises(): Flow<List<ExerciseEntity>>

    /**
     * Возвращает упражнение по его идентификатору или `null`, если запись не найдена.
     *
     * @param id идентификатор упражнения
     * @return ExerciseEntity? найденная сущность или null
     */
    @Query("SELECT * FROM exercises WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): ExerciseEntity?

    /**
     * Вставляет или обновляет список упражнений. В случае конфликта (совпадение PK) запись заменяется.
     *
     * @param exercises список сущностей для вставки
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exercises: List<ExerciseEntity>)

    /**
     * Вставляет или обновляет одно упражнение. При совпадении PK выполняется замена.
     * @param exercise сущность упражнения для сохранения
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exercise: ExerciseEntity)

    /**
     * Удаляет все записи из таблицы упражнений.
     * Используйте с осторожностью — операция без возможности восстановления.
     */
    @Query("DELETE FROM exercises")
    suspend fun clearAll()
}
