package com.example.fitnessapp.data.remote

import com.example.fitnessapp.data.remote.dto.LoginRequestDto
import com.example.fitnessapp.data.remote.dto.LoginResponse
import com.example.fitnessapp.data.remote.dto.ActivityRequest
import com.example.fitnessapp.data.remote.dto.ActivityDto
import com.example.fitnessapp.data.remote.dto.HistoryDto
import com.example.fitnessapp.data.remote.dto.UserDto
import com.example.fitnessapp.data.remote.dto.ExerciseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import okhttp3.ResponseBody

interface AutorizeApiRetrofit {
    @POST("auth/register")
    suspend fun register(
        @Body request: LoginRequestDto
    ): LoginResponse

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): LoginResponse

    // Activities endpoints (kept for compatibility)
    @GET("activities")
    suspend fun getActivities(): List<ActivityDto>

    @POST("activities")
    suspend fun createActivity(
        @Body request: ActivityRequest
    ): ActivityDto

    @GET("activities/{id}")
    suspend fun getActivityById(
        @Path("id") id: Long
    ): ActivityDto

    @PUT("activities/{id}")
    suspend fun updateActivity(
        @Path("id") id: Long,
        @Body request: ActivityRequest
    ): ActivityDto

    @DELETE("activities/{id}")
    suspend fun deleteActivity(
        @Path("id") id: Long
    ): ResponseBody

    // User history (backward compatible signature)
    @GET("users/{id}/history")
    suspend fun getHistory(
        @Path("id") id: Int
    ): List<HistoryDto>

    // User profile update (kept, but server might ignore unknown fields)
    @PUT("users/{id}")
    suspend fun updateUser(
        @Path("id") id: Int,
        @Body request: UserDto
    ): UserDto
}