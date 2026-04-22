package com.example.fitnessapp.data.remote

import com.example.fitnessapp.data.remote.dto.ExerciseDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.post
import io.ktor.client.request.get
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText

/**
 * Простой Ktor-клиент для взаимодействия с сервером (регистрация/авторизация/получение упражнений).
 * Используем парсинг через Gson, чтобы не требовать плагина kotlinx.serialization в проекте.
 */
class KtorRemoteApi(baseUrl: String) {
    private val client = HttpClient(CIO)
    private val base = baseUrl.trimEnd('/')
    private val gson = Gson()

    suspend fun fetchExercises(): List<ExerciseDto> {
        val responseText = client.get("$base/exercises").bodyAsText()
        val listType = object : TypeToken<List<ExerciseDto>>() {}.type
        return gson.fromJson(responseText, listType)
    }

    suspend fun registerUser(payload: Any) = client.post("$base/register") { setBody(gson.toJson(payload)) }

    suspend fun loginUser(payload: Any) = client.post("$base/login") { setBody(gson.toJson(payload)) }

    suspend fun updateSettings(payload: Any) = client.post("$base/settings") { setBody(gson.toJson(payload)) }
}
