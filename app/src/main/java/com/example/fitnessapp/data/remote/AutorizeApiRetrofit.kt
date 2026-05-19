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
    @POST("registration")
    suspend fun register(
        @Body request: UserDto
    ): UserDto

    @POST("login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): LoginResponse

    @GET("users")
    suspend fun getUsers(
        @Query("limit") limit: Int = 30,
        @Query("skip") skip: Int = 0
    ): UsersResponse

    @GET("users/{id}")
    suspend fun getUserById(
        @Path("id") id: Int
    ): UserDto

    @POST("users/{id}/callories")
    suspend fun postCalories(
        @Path("id") id: Int,
        @Body request: CaloriesRequest
    ): ResponseBody

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