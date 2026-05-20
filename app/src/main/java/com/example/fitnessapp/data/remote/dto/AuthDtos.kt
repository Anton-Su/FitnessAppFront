package com.example.fitnessapp.data.remote.dto

/**
 * Запрос для авторизации: email и пароль.
 */
data class LoginRequestDto(
    val email: String,
    val password: String
)

/**
 * Пользователь приложения: для регистрации и профиля.
 */
data class UserDto(
    val id: Int = 0,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val height: Double = 0.0,
    val weight: Double = 0.0,
    val age: Int = 0,
)

/**
 * Ответ на авторизацию.
 */
data class LoginResponse(
    val accessToken: String = "",
    val user: UserDto? = null
)

/**
 * Ответ со списком пользователей.
 */
data class UsersResponse(
    val users: List<UserDto> = emptyList(),
    val total: Int = 0,
    val skip: Int = 0,
    val limit: Int = 0
)


/**
 * Тело запроса на отправку калорий.
 */
data class CaloriesRequest(
    val steps: Int,
    val calories: Int,
    val date: String
)


/**
 * DTO для записи истории активности (при получении с сервера).
 */
data class HistoryDto(
    val id: Long = 0,
    val date: String = "",
    val calories: Int = 0,
    val steps: Int = 0
)


