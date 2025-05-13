package com.example.trimly.utils

import android.content.Context
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
     * @param context Контекст додатку
     * @param isDebug Чи запущено додаток в режимі налагодження
     */
    fun init(context: Context, isDebug: Boolean) {
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

    /**
     * Логує повідомлення рівня ERROR.
     *
     * @param throwable Виняток для логування
     * @param message Повідомлення для логування
     * @param metadata Додаткові дані для логування
     */
    fun logError(throwable: Throwable, message: String, metadata: Map<String, Any> = emptyMap()) {
        val metadataString = metadata.entries.joinToString(", ") { "${it.key}=${it.value}" }
        Timber.e(throwable, "$message [$metadataString]")
    }

    /**
     * Логує користувацький подію.
     *
     * @param eventName Ім'я події
     * @param metadata Додаткові дані для логування
     */
    fun logUserEvent(eventName: String, metadata: Map<String, Any> = emptyMap()) {
        val metadataString = metadata.entries.joinToString(", ") { "${it.key}=${it.value}" }
        Timber.i("User Event: $eventName [$metadataString]")
    }

    /**
     * Логує мережевий запит.
     *
     * @param url URL запиту
     * @param method Метод запиту
     * @param metadata Додаткові дані для логування
     */
    fun logNetworkRequest(url: String, method: String, metadata: Map<String, Any> = emptyMap()) {
        val metadataString = metadata.entries.joinToString(", ") { "${it.key}=${it.value}" }
        Timber.d("Network Request: $method $url [$metadataString]")
    }

    /**
     * Логує навігацію.
     *
     * @param from Початкова точка
     * @param to Кінцева точка
     * @param metadata Додаткові дані для логування
     */
    fun logNavigation(from: String, to: String, metadata: Map<String, Any> = emptyMap()) {
        val metadataString = metadata.entries.joinToString(", ") { "${it.key}=${it.value}" }
        Timber.d("Navigation: $from -> $to [$metadataString]")
    }

    /**
     * Логує метрику продуктивності.
     *
     * @param metricName Ім'я метрики
     * @param value Значення метрики
     * @param metadata Додаткові дані для логування
     */
    fun logPerformanceMetric(metricName: String, value: Long, metadata: Map<String, Any> = emptyMap()) {
        val metadataString = metadata.entries.joinToString(", ") { "${it.key}=${it.value}" }
        Timber.d("Performance: $metricName = ${value}ms [$metadataString]")
    }
}
