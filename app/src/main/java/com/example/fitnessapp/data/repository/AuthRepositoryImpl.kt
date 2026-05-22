package com.example.fitnessapp.data.repository

import android.util.Log
import com.example.fitnessapp.data.preferences.TokenManager
import com.example.fitnessapp.data.remote.AutorizeApiRetrofit
import com.example.fitnessapp.data.remote.dto.HistoryDto
import com.example.fitnessapp.data.remote.dto.UserDto
import com.example.fitnessapp.data.remote.dto.LoginRequestDto
import com.example.fitnessapp.data.remote.dto.LoginResponse
import com.example.fitnessapp.domain.repository.AuthRepository
import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException

class AuthRepositoryImpl(
    private val api: AutorizeApiRetrofit,
    private val tokenManager: TokenManager
) : AuthRepository {
    companion object {
        private const val TAG = "AuthRepository"
        private val gson = Gson()
    }

    override suspend fun register(firstName: String, email: String, age: Int, password: String): Result<Int> {
        return try {
            // Server expects username/email/password — use LoginRequestDto for compatibility
            val request = LoginRequestDto(username = firstName, password = password)
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
            Log.d(TAG, "Attempting login with email: $email")
            val request = LoginRequestDto(username = email, password = password)
            Log.d(TAG, "Sending login request with credentials: username=$email")
            val response: LoginResponse = api.login(request)
            // server may return token or accessToken
            val token = response.token.ifBlank { response.accessToken }
            token.takeIf { it.isNotBlank() }?.let { tokenManager.saveAccessToken(it) }
            val id = when {
                response.user_id > 0 -> response.user_id
                (response.user?.id ?: 0) > 0 -> response.user!!.id
                (response.user?.user_id ?: 0) > 0 -> response.user!!.user_id
                else -> 0
            }
            Log.d(TAG, "Login successful, user ID: $id")
            Result.success(id)
        } catch (e: HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string() ?: "No error body"
            } catch (_: Exception) {
                "Could not read error body"
            }
            Log.e(TAG, "HTTP error while login: Code ${e.code()}\nError body: $errorBody", e)
            Result.failure(e)
        } catch (e: IOException) {
            Log.e(TAG, "Network error while login: ${e.message}", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error while login: ${e.message}", e)
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

    override suspend fun getHistory(userId: Int): Result<List<HistoryDto>> {
        return try {
            val remote = api.getHistory(userId)
            Log.d(TAG, "GET /users/$userId/history response body: ${gson.toJson(remote)}")
            Result.success(remote)
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error while loading history: ${e.code()}", e)
            Result.failure(e)
        } catch (e: IOException) {
            Log.e(TAG, "Network error while loading history", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error while loading history", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteUser(userId: Int): Result<Unit> {
        return try {
            api.deleteUser(userId)
            // Clear token immediately after successful deletion to prevent 401 errors on subsequent requests
            tokenManager.clearTokens()
            Log.d(TAG, "User deleted and token cleared successfully")
            Result.success(Unit)
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error while deleting user: ${e.code()}", e)
            Result.failure(e)
        } catch (e: IOException) {
            Log.e(TAG, "Network error while deleting user", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error while deleting user", e)
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        tokenManager.clearTokens()
    }

    override suspend fun exportRemoteHistoryJson(userId: Int): Result<String> {
        return try {
            val remoteHistory = api.getHistory(userId)
            val json = gson.toJson(remoteHistory)
            Log.d(TAG, "exportRemoteHistoryJson: exported ${remoteHistory.size} records, JSON length=${json.length}")
            Result.success(json)
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error while exporting remote history: ${e.code()}", e)
            Result.failure(e)
        } catch (e: IOException) {
            Log.e(TAG, "Network error while exporting remote history", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error while exporting remote history", e)
            Result.failure(e)
        }
    }
}
