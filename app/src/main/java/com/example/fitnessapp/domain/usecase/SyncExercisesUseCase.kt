package com.example.fitnessapp.domain.usecase

import com.example.fitnessapp.data.local.entity.ExerciseEntity
import com.example.fitnessapp.data.repository.LocalExerciseRepositoryImpl
import com.example.fitnessapp.data.repository.RemoteExerciseRepository

/**
 * UseCase для синхронизации упражнений с удалённого сервера.
 *
 * Загружает данные через [RemoteExerciseRepository] и сохраняет их в локальный репозиторий
 * через [LocalExerciseRepositoryImpl]. Полезен для первичной или периодической синхронизации данных.
 */
class SyncExercisesUseCase(
    private val remoteRepo: RemoteExerciseRepository,
    private val localRepo: LocalExerciseRepositoryImpl
) {
    /**
     * Выполняет синхронизацию: скачивает список DTO с сервера, маппит в [ExerciseEntity] и сохраняет локально.
     */
    suspend operator fun invoke() {
        val remote = remoteRepo.fetchExercises()
        val entities = remote.map { ExerciseEntity(it.id, it.title, it.description, it.videoUrl, it.type) }
        localRepo.insertAll(entities)
    }
}
