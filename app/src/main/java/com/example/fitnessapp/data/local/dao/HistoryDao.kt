package com.example.fitnessapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fitnessapp.data.local.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO для работы с историей активности пользователя (`user_history`).
 *
 * Содержит методы для получения истории в виде потока (Flow), вставки записей,
 * удаления конкретной записи и очистки всей таблицы.
 *
 * Каждый элемент [HistoryEntity] содержит поля: id, date (ISO yyyy-MM-dd), calories и steps.
 */
@Dao
interface HistoryDao {
    /**
     * Возвращает поток со списком записей истории, отсортированных по дате (DESC).
     * @return Flow<List<HistoryEntity>> поток списка историй
     */
    @Query("SELECT * FROM user_history ORDER BY date DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    /**
     * Вставляет или обновляет запись истории. При конфликте (совпадение PK) запись заменяется.
     * @param history запись истории для сохранения
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: HistoryEntity)

    /**
     * Удаляет запись истории по идентификатору.
     * @param id идентификатор записи (PK)
     */
    @Query("DELETE FROM user_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    /**
     * Удаляет все записи истории из таблицы.
     * Операция необратима — используйте осторожно.
     */
    @Query("DELETE FROM user_history")
    suspend fun clearAll()
}
