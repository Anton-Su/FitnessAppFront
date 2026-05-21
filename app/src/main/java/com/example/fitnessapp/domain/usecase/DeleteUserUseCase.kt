package com.example.fitnessapp.domain.usecase

import com.example.fitnessapp.domain.repository.AuthRepository

class DeleteUserUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke(userId: Int): Result<Unit> = repo.deleteUser(userId)
}

