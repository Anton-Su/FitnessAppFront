package com.example.fitnessapp.presentation.util

/**
 * Валидирует email адрес по следующим правилам:
 * - Допустимая длина 3-254 символа
 * - Должен содержать exactly одну "@"
 * - Перед "@" минимум 1 символ (username)
 * - После "@" минимум 3 символа (domain.extension)
 * - Поддерживаются буквы, цифры, точки, дефисы и подчеркивания
 * - Не допускаются пробелы
 */
object EmailValidator {
    private val emailRegex = Regex(
        """^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$"""
    )

    fun isValid(email: String): Boolean {
        if (email.isBlank()) return false

        // Длина email
        if (email.length < 3 || email.length > 254) return false

        // Не должно быть пробелов
        if (email.contains(" ")) return false

        // Количество '@' должно быть ровно одно
        if (email.count { it == '@' } != 1) return false

        // Проверка по regex
        return emailRegex.matches(email.lowercase())
    }

    fun getErrorMessage(email: String): String {
        return when {
            email.isBlank() -> ""
            email.length < 3 || email.length > 254 -> "Email: от 3 до 254 символов"
            email.contains(" ") -> "Email не должен содержать пробелы"
            email.count { it == '@' } != 1 -> "Email должен содержать ровно одну '@'"
            !email.contains("@") -> "Email должен содержать '@'"
            !email.contains(".") -> "Email должен содержать доменное имя с точкой"
            !isValid(email) -> "Некорректный формат email"
            else -> ""
        }
    }
}

