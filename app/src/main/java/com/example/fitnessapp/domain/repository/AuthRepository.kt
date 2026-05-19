package com.example.fitnessapp.domain.repository

interface AuthRepository {
    suspend fun register(firstName: String, email: String, age: Int, password: String): Result<Unit>
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun logout()
}
