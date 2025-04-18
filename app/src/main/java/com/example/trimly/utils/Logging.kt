package com.example.trimly.utils

import timber.log.Timber

/**
 * Утиліта для логування в додатку.
 * 
 * Надає зручний інтерфейс для логування через Timber.
 * Автоматично додає теги та форматування.
 */
object Logging {
    /**
     * Ініціалізує систему логування.
     * 
     * @param isDebug Чи запущено додаток в режимі налагодження
     */
    fun init(isDebug: Boolean) {
        if (isDebug) {
            Timber.plant(Timber.DebugTree())
        }
    }

    /**
     * Логує повідомлення рівня DEBUG.
     * 
     * @param message Повідомлення для логування
     * @param args Додаткові аргументи для форматування
     */
    fun d(message: String, vararg args: Any) {
        Timber.d(message, *args)
    }

    /**
     * Логує повідомлення рівня INFO.
     * 
     * @param message Повідомлення для логування
     * @param args Додаткові аргументи для форматування
     */
    fun i(message: String, vararg args: Any) {
        Timber.i(message, *args)
    }

    /**
     * Логує повідомлення рівня WARNING.
     * 
     * @param message Повідомлення для логування
     * @param args Додаткові аргументи для форматування
     */
    fun w(message: String, vararg args: Any) {
        Timber.w(message, *args)
    }

    /**
     * Логує повідомлення рівня ERROR.
     * 
     * @param message Повідомлення для логування
     * @param throwable Виняток для логування
     * @param args Додаткові аргументи для форматування
     */
    fun e(message: String, throwable: Throwable? = null, vararg args: Any) {
        if (throwable != null) {
            Timber.e(throwable, message, *args)
        } else {
            Timber.e(message, *args)
        }
    }
} 