package com.example.fitnessapp.data.remote

import android.content.Context
import com.example.fitnessapp.data.preferences.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitClient {
    private const val BASE_URL = "https://dummyjson.com/"

    @Volatile
    private var tokenManager: TokenManager? = null

    @Volatile
    private var authenticatedAuthApi: AutorizeApiRetrofit? = null

    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    fun init(context: Context) {
        if (tokenManager != null) return
        synchronized(this) {
            if (tokenManager == null) {
                tokenManager = TokenManager(context.applicationContext)
                authenticatedAuthApi = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(
                        OkHttpClient.Builder()
                            .addInterceptor(logging)
                            .addInterceptor(AuthInterceptor(tokenManager!!))
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .build()
                    )
                    .build()
                    .create(AutorizeApiRetrofit::class.java)
            }
        }
    }

    val exerciseApi: ExerciseApiRetrofit = retrofit.create(ExerciseApiRetrofit::class.java)
    val authApi: AutorizeApiRetrofit
        get() = authenticatedAuthApi ?: retrofit.create(AutorizeApiRetrofit::class.java)
}