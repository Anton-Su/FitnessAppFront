package com.example.fitnessapp.data.remote


import com.example.fitnessapp.data.remote.dto.LoginResponse
import com.example.fitnessapp.data.remote.dto.LoginRequestDto
import com.example.fitnessapp.data.remote.dto.CaloriesRequest
import com.example.fitnessapp.data.remote.dto.UserDto
import com.example.fitnessapp.data.remote.dto.UsersResponse
import com.example.fitnessapp.data.remote.dto.ExerciseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import okhttp3.ResponseBody

interface AutorizeApiRetrofit {
    @POST("authorization/registration")
    suspend fun register(
        @Body request: UserDto
    ): UserDto

    @POST("authorization/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): LoginResponse


    @POST("users/{id}/callories")
    suspend fun postCalories(
        @Path("id") id: Int,
        @Body request: CaloriesRequest
    ): ResponseBody

    @GET("users/{id}/history")
    suspend fun getHistory(
        @Path("id") id: Int
    ): List<com.example.fitnessapp.data.remote.dto.HistoryDto>

    @PUT("users/{id}")
    suspend fun updateUser(
        @Path("id") id: Int,
        @Body request: UserDto
    ): UserDto

    @GET("users/{id}/recommendation")
    suspend fun getRecommendation(
        @Path("id") id: Int
    ): ExerciseDto
}