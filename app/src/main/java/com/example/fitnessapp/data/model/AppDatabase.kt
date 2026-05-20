package com.example.fitnessapp.data.model

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.fitnessapp.data.local.dao.HistoryDao
import com.example.fitnessapp.data.local.dao.RecommendationDao
import com.example.fitnessapp.data.local.dao.UserSettingsDao
import com.example.fitnessapp.data.local.dao.ExerciseDao
import com.example.fitnessapp.data.local.entity.HistoryEntity
import com.example.fitnessapp.data.local.entity.RecommendationEntity
import com.example.fitnessapp.data.local.entity.UserSettingsEntity
import com.example.fitnessapp.data.local.entity.ExerciseEntity

/**
 * Главная база данных приложения (Room).
 */
@Database(entities = [HistoryEntity::class, UserSettingsEntity::class, ExerciseEntity::class, RecommendationEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun userSettingsDao(): UserSettingsDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun recommendationDao(): RecommendationDao

    companion object {
        private const val DB_NAME = "fitness_app.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
