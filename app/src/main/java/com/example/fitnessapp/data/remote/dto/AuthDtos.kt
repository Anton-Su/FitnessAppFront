package com.example.fitnessapp.data.remote.dto

/**
 * Универсальные DTO, которые покрывают как старые внутренние ожидания приложения,
 * так и новый формат ответов сервера. Поля опциональны и имеют значения по умолчанию —
 * это обеспечивает поведение с плейсхолдерами, если сервер не вернёт некоторые поля.
 */

// --- Auth / User ---
data class LoginRequestDto(
    val username: String = "",
    val email: String = "",
    val password: String = ""
)

data class UserDto(
    // сервер может возвращать user_id или id — поддерживаем оба
    val id: Int = 0,
    val user_id: Int = 0,
    val username: String = "",
    // старые поля, которые могли использоваться в коде
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val height: Double = 0.0,
    val weight: Double = 0.0,
    val age: Int = 0
)

data class LoginResponse(
    // новый формат
    val user_id: Int = 0,
    val username: String = "",
    val message: String = "",
    val token: String = "",
    val tokenType: String = "Bearer",
    val expiresIn: Int = 3600,
    // старый формат (для обратной совместимости)
    val accessToken: String = "",
    val user: UserDto? = null
)

data class UsersResponse(
    val users: List<UserDto> = emptyList(),
    val total: Int = 0,
    val skip: Int = 0,
    val limit: Int = 0
)

// --- Activity / Calories ---
data class CaloriesRequest(
    val steps: Int = 0,
    val calories: Int = 0,
    val date: String = ""
)

data class ActivityRequest(
    val user: UserDto? = null,
    val activity_date: String = "",
    val steps: Int = 0,
    val burnt: Int = 0,
    val goal_achieved: Boolean = false
)

data class ActivityDto(
    val activity_id: Long = 0,
    val user: UserDto? = null,
    val activity_date: String = "",
    val steps: Int = 0,
    val burnt: Int = 0,
    val goal_achieved: Boolean = false
)

// --- History ---
data class HistoryDto(
    val id: Long = 0,
    val date: String = "",
    val calories: Int = 0,
    val steps: Int = 0
)


