package com.example.fitnessapp.data.repository

import android.util.Log
import com.example.fitnessapp.data.preferences.TokenManager
import com.example.fitnessapp.data.remote.AutorizeApiRetrofit
import com.example.fitnessapp.data.remote.dto.UserDto
import com.example.fitnessapp.data.remote.dto.LoginRequestDto
import com.example.fitnessapp.domain.repository.AuthRepository
import retrofit2.HttpException
import java.io.IOException

class AuthRepositoryImpl(
    private val api: AutorizeApiRetrofit,
    private val tokenManager: TokenManager
) : AuthRepository {
    companion object {
        private const val TAG = "AuthRepository"
    }

    override suspend fun register(firstName: String, email: String, age: Int, password: String): Result<Unit> {
        return try {
            api.register(
                UserDto(
                    firstName = firstName,
                    email = email,
                    age = age,
                    password = password
                )
            )
            Result.success(Unit)
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error while registering user: ${e.code()}", e)
            Result.failure(e)
        } catch (e: IOException) {
            Log.e(TAG, "Network error while registering user", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error while registering user", e)
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            val response = api.login(LoginRequestDto(email = email, password = password))
            response.accessToken.takeIf { it.isNotBlank() }?.let { tokenManager.saveAccessToken(it) }
            Result.success(Unit)
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error while login: ${e.code()}", e)
            Result.failure(e)
        } catch (e: IOException) {
            Log.e(TAG, "Network error while login", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error while login", e)
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        tokenManager.clearTokens()
    }
}
