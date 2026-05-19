package com.example.fitnessapp.data.repository

import android.util.Log
import com.example.fitnessapp.data.remote.ExerciseApiRetrofit
import com.example.fitnessapp.data.remote.RetrofitClient
import com.example.fitnessapp.data.remote.dto.toDomain
import com.example.fitnessapp.domain.model.Exercise
import com.example.fitnessapp.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

/**
 * Репозиторий упражнений, работающий через Retrofit.
 */
class ExerciseRepositoryImpl(
    private val api: ExerciseApiRetrofit = RetrofitClient.exerciseApi
) : ExerciseRepository {
    companion object {
        private const val TAG = "ExerciseRepository"
    }

    /**
     * Возвращает поток упражнений, загруженных с сервера.
     */
    override fun getExercises(): Flow<List<Exercise>> = flow {
        try {
            emit(api.getExercises().map { it.toDomain() })
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error while loading exercises: ${e.code()}", e)
            emit(emptyList())
        } catch (e: IOException) {
            Log.e(TAG, "Network error while loading exercises", e)
            emit(emptyList())
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error while loading exercises", e)
            emit(emptyList())
        }
    }

    /**
     * Возвращает упражнение по идентификатору с сервера или null, если не найдено.
     */
    override suspend fun getExerciseById(id: Int): Exercise? {
        return try {
            api.getExerciseById(id).toDomain()
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error while loading exercise by id=$id: ${e.code()}", e)
            null
        } catch (e: IOException) {
            Log.e(TAG, "Network error while loading exercise by id=$id", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error while loading exercise by id=$id", e)
            null
        }
    }

    /**
     * Возвращает упражнения отфильтрованные по типу.
     */
    override suspend fun getExercisesByType(type: String): List<Exercise> {
        return try {
            api.getExercisesByType(type).map { it.toDomain() }
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error while loading exercises by type=$type: ${e.code()}", e)
            emptyList()
        } catch (e: IOException) {
            Log.e(TAG, "Network error while loading exercises by type=$type", e)
            emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error while loading exercises by type=$type", e)
            emptyList()
        }
    }

    /**
     * Возвращает рекомендуемое упражнение для пользователя.
     */
    override suspend fun getRecommendation(userId: Int): Exercise? {
        val authApi = RetrofitClient.authApi
        return try {
            authApi.getRecommendation(userId).toDomain()
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error while loading recommendation for user=$userId: ${e.code()}", e)
            null
        } catch (e: IOException) {
            Log.e(TAG, "Network error while loading recommendation for user=$userId", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error while loading recommendation for user=$userId", e)
            null
        }
    }
}
