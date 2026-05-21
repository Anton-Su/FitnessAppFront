package com.example.fitnessapp.domain.model

/**
 * Пол пользователя для экранов, хранения и расчётов.
 */
enum class Gender(val code: String) {
    MALE("male"),
    FEMALE("female");

    companion object {
        fun fromCode(code: String?): Gender = when (code?.lowercase()) {
            MALE.code -> MALE
            FEMALE.code -> FEMALE
            else -> FEMALE
        }
    }
}

