package com.example.fitnessapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fitnessapp.data.local.entity.RecommendationEntity

/**
 * DAO для локального кэша рекомендаций дня.
 */
@Dao
interface RecommendationDao {
    @Query("SELECT * FROM recommendations WHERE user_id = :userId LIMIT 1")
    suspend fun getByUserId(userId: Int): RecommendationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(recommendation: RecommendationEntity)

    @Query("DELETE FROM recommendations")
    suspend fun clearAll()
}

