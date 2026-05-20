package com.example.fitnessapp.data.repository

import android.util.Log
import com.example.fitnessapp.data.local.dao.RecommendationDao
import com.example.fitnessapp.data.local.mapper.toDomain
import com.example.fitnessapp.data.local.mapper.toEntity
import com.example.fitnessapp.data.local.mapper.toRecommendationEntity
import com.example.fitnessapp.data.remote.ExerciseApiRetrofit
import com.example.fitnessapp.data.remote.RetrofitClient
import com.example.fitnessapp.data.remote.dto.toDomain as dtoToDomain
import com.example.fitnessapp.domain.model.Exercise
import com.example.fitnessapp.domain.repository.ExerciseRepository
import com.example.fitnessapp.domain.repository.LocalExerciseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

/**
 * Репозиторий упражнений, работающий через Retrofit.
 */
class ExerciseRepositoryImpl(
    private val api: ExerciseApiRetrofit = RetrofitClient.exerciseApi,
    private val localRepo: LocalExerciseRepository,
    private val recommendationDao: RecommendationDao
) : ExerciseRepository {
    companion object {
        private const val TAG = "ExerciseRepository"
    }

    /**
     * Возвращает поток упражнений, сначала из Room, затем обновляет их с сервера.
     */
    override fun getExercises(): Flow<List<Exercise>> = flow {
        emit(localRepo.getAll().first().map { it.toDomain() })
        try {
            val remote = api.getExercises().map { it.dtoToDomain() }
            localRepo.insertAll(remote.map { it.toEntity() })
            emit(localRepo.getAll().first().map { it.toDomain() })
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error while loading exercises: ${e.code()}", e)
        } catch (e: IOException) {
            Log.e(TAG, "Network error while loading exercises", e)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error while loading exercises", e)
        }
    }

    /**
     * Возвращает упражнение по идентификатору с сервера или null, если не найдено.
     */
    override suspend fun getExerciseById(id: Int): Exercise? {
        return try {
            api.getExerciseById(id).dtoToDomain()
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error while loading exercise by id=$id: ${e.code()}", e)
            localRepo.getById(id)?.toDomain()
        } catch (e: IOException) {
            Log.e(TAG, "Network error while loading exercise by id=$id", e)
            localRepo.getById(id)?.toDomain()
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error while loading exercise by id=$id", e)
            localRepo.getById(id)?.toDomain()
        }
    }

    /**
     * Возвращает упражнения отфильтрованные по типу.
     */
    override suspend fun getExercisesByType(type: String): List<Exercise> {
        return try {
            api.getExercisesByType(type).map { it.dtoToDomain() }
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error while loading exercises by type=$type: ${e.code()}", e)
            localExercisesByType(type)
        } catch (e: IOException) {
            Log.e(TAG, "Network error while loading exercises by type=$type", e)
            localExercisesByType(type)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error while loading exercises by type=$type", e)
            localExercisesByType(type)
        }
    }

    /**
     * Возвращает рекомендуемое упражнение для пользователя.
     */
    override suspend fun getRecommendation(userId: Int): Exercise? {
        // Server may no longer provide a recommendation endpoint; prefer local cache
        return try {
            localRecommendation(userId)
        } catch (e: Exception) {
            Log.e(TAG, "Error while loading local recommendation for user=$userId", e)
            null
        }
    }

    private suspend fun localExercisesByType(type: String): List<Exercise> {
        return localRepo.getAll().first()
            .map { it.toDomain() }
            .filter { it.type.equals(type, ignoreCase = true) }
    }

    private suspend fun localRecommendation(userId: Int): Exercise? {
        return recommendationDao.getByUserId(userId)?.toDomain()
    }
}
