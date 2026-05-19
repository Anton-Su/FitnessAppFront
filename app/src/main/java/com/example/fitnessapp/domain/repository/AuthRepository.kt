package com.example.fitnessapp.domain.repository

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
    suspend fun logout()
}
