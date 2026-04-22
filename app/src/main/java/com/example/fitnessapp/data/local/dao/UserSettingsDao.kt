package com.example.fitnessapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fitnessapp.data.local.entity.UserSettingsEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO для хранения дополнительных настроек пользователя в таблице `user_settings`.
 *
 * Предоставляет поток для получения единственной записи настроек (LIMIT 1), метод upsert для
 * вставки/обновления и очистки таблицы.
 */
@Dao
interface UserSettingsDao {
    /**
     * Возвращает поток с текущей записью пользовательских настроек или null, если запись отсутствует.
     * @return Flow<UserSettingsEntity?> поток опциональной сущности настроек
     */
    @Query("SELECT * FROM user_settings LIMIT 1")
    fun getSettings(): Flow<UserSettingsEntity?>

    /**
     * Вставляет или обновляет запись настроек (upsert). В случае конфликта запись заменяется.
     * @param settings сущность настроек для сохранения
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(settings: UserSettingsEntity)

    /**
     * Удаляет все записи настроек (обычно используется при сбросе данных пользователя).
     */
    @Query("DELETE FROM user_settings")
    suspend fun clear()
}
