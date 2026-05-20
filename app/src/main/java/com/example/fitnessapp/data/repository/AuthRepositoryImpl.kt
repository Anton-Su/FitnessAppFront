package com.example.fitnessapp.data.repository

import android.util.Log
import com.example.fitnessapp.data.preferences.TokenManager
import com.example.fitnessapp.data.remote.AutorizeApiRetrofit
import com.example.fitnessapp.data.remote.dto.UserDto
import com.example.fitnessapp.data.remote.dto.LoginRequestDto
import com.example.fitnessapp.data.remote.dto.LoginResponse
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

    override suspend fun register(firstName: String, email: String, age: Int, password: String): Result<Int> {
        return try {
            // Server expects username/email/password — use LoginRequestDto for compatibility
            val request = LoginRequestDto(username = firstName, email = email, password = password)
            val response: LoginResponse = api.register(request)
            // prefer server user_id, fallback to user.id or 0
            val id = when {
                response.user_id > 0 -> response.user_id
                (response.user?.id ?: 0) > 0 -> response.user!!.id
                (response.user?.user_id ?: 0) > 0 -> response.user!!.user_id
                else -> 0
            }
            // save token if present (try multiple fields)
            val token = response.token.ifBlank { response.accessToken }
            token.takeIf { it.isNotBlank() }?.let { tokenManager.saveAccessToken(it) }
            Result.success(id)
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

    override suspend fun login(email: String, password: String): Result<Int> {
        return try {
            val response: LoginResponse = api.login(LoginRequestDto(email = email, password = password))
            // server may return token or accessToken
            val token = response.token.ifBlank { response.accessToken }
            token.takeIf { it.isNotBlank() }?.let { tokenManager.saveAccessToken(it) }
            val id = when {
                response.user_id > 0 -> response.user_id
                (response.user?.id ?: 0) > 0 -> response.user!!.id
                (response.user?.user_id ?: 0) > 0 -> response.user!!.user_id
                else -> 0
            }
            Result.success(id)
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

    override suspend fun updateUserProfile(
        userId: Int,
        firstName: String,
        email: String,
        age: Int,
        height: Double,
        weight: Double,
        password: String
    ): Result<Unit> {
        return try {
            api.updateUser(
                id = userId,
                request = UserDto(
                    id = userId,
                    firstName = firstName,
                    email = email,
                    age = age,
                    height = height,
                    weight = weight,
                    password = password
                )
            )
            Result.success(Unit)
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error while updating user profile: ${e.code()}", e)
            Result.failure(e)
        } catch (e: IOException) {
            Log.e(TAG, "Network error while updating user profile", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error while updating user profile", e)
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        tokenManager.clearTokens()
    }
}
