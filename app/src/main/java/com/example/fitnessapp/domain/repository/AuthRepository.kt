package com.example.fitnessapp.domain.repository

import com.example.fitnessapp.data.remote.dto.HistoryDto

interface AuthRepository {
    suspend fun register(firstName: String, email: String, age: Int, password: String): Result<Int>
    suspend fun login(email: String, password: String): Result<Int>
    suspend fun updateUserProfile(
        userId: Int,
        firstName: String,
        email: String,
        age: Int,
        height: Double,
        weight: Double,
        password: String
    ): Result<Unit>
    suspend fun getHistory(userId: Int): Result<List<HistoryDto>>
    suspend fun deleteUser(userId: Int): Result<Unit>
    suspend fun logout()
}
