package com.example.trimly.utils

import android.content.Context
import android.widget.Toast
import java.io.IOException
import java.net.UnknownHostException
import android.Manifest

object ErrorHandler {
    fun handleError(context: Context, throwable: Throwable, metadata: Map<String, Any> = emptyMap()) {
        when (throwable) {
            is IOException, is UnknownHostException -> {
                Logging.logError(throwable, "Network error", metadata)
                showToast(context, "Помилка мережі. Перевірте підключення до інтернету.")
            }
            is SecurityException -> {
                Logging.logError(throwable, "Permission error", metadata)
                showToast(context, "Помилка доступу. Перевірте налаштування дозволів.")
            }
            is IllegalArgumentException -> {
                Logging.logError(throwable, "Validation error", metadata)
                showToast(context, "Помилка валідації даних: ${throwable.message}")
            }
            else -> {
                Logging.logError(throwable, "Unexpected error", metadata)
                showToast(context, "Сталася неочікувана помилка. Спробуйте ще раз.")
            }
        }
    }

    private fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun validateUserData(email: String, password: String): Boolean {
        if (email.isBlank()) {
            Logging.logError(
                IllegalArgumentException("Empty email"),
                "Invalid user data",
                mapOf("field" to "email")
            )
            return false
        }
        if (password.isBlank()) {
            Logging.logError(
                IllegalArgumentException("Empty password"),
                "Invalid user data",
                mapOf("field" to "password")
            )
            return false
        }
        return true
    }

    fun handleLocationError(context: Context, throwable: Throwable) {
        when (throwable) {
            is SecurityException -> {
                Logging.logError(throwable, "Location permission denied", mapOf(
                    "permission" to Manifest.permission.ACCESS_FINE_LOCATION
                ))
                showToast(context, "Для роботи з геолокацією потрібен дозвіл на доступ до місцезнаходження")
            }
            else -> {
                Logging.logError(throwable, "Location error", mapOf(
                    "error_type" to throwable.javaClass.simpleName
                ))
                showToast(context, "Помилка отримання місцезнаходження")
            }
        }
    }
} 