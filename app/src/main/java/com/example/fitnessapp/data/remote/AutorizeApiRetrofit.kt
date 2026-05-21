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

    @POST("activities")
    suspend fun createActivity(
        @Body request: ActivityRequest
    ): ActivityDto

//    @GET("activities/{id}")
//    suspend fun getActivityById(
//        @Path("id") id: Long
//    ): ActivityDto


    // User history (backward compatible signature)
    @GET("journal/user/{id}/history")
    suspend fun getHistory(
        @Path("id") id: Int
    ): List<HistoryDto>

    @GET("users/{id}/recommendation")
    suspend fun getRecommendation(
        @Path("id") id: Int
    ): ExerciseDto

    // User profile update (kept, but server might ignore unknown fields)
    @PUT("users/{id}")
    suspend fun updateUser(
        @Path("id") id: Int,
        @Body request: UserDto
    ): UserDto

    @DELETE("users/{id}")
    suspend fun deleteUser(
        @Path("id") id: Int
    ): ResponseBody
}