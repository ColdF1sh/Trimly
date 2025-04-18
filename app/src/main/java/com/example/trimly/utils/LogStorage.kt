package com.example.trimly.utils

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Клас для зберігання логів між сеансами
 */
object LogStorage {
    private const val LOG_DIR = "logs"
    private const val MAX_LOG_FILES = 5
    private const val MAX_LOG_SIZE = 1024 * 1024 // 1MB
    
    private lateinit var logDir: File
    
    /**
     * Ініціалізація сховища логів
     */
    fun init(context: Context) {
        logDir = File(context.filesDir, LOG_DIR)
        if (!logDir.exists()) {
            logDir.mkdirs()
        }
        
        // Очищення старих логів
        cleanupOldLogs()
    }
    
    /**
     * Збереження логу
     * @param level рівень логу
     * @param tag тег
     * @param message повідомлення
     * @param throwable виняток (опціонально)
     */
    fun saveLog(level: Int, tag: String, message: String, throwable: Throwable? = null) {
        try {
            val currentLogFile = getCurrentLogFile()
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
                .format(Date())
            
            val logEntry = buildString {
                append("$timestamp [${getLevelString(level)}] $tag: $message")
                throwable?.let {
                    append("\n${Log.getStackTraceString(it)}")
                }
                append("\n")
            }
            
            FileOutputStream(currentLogFile, true).use { output ->
                output.write(logEntry.toByteArray())
            }
            
            // Перевірка розміру файлу
            if (currentLogFile.length() > MAX_LOG_SIZE) {
                rotateLogs()
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to save log")
        }
    }
    
    /**
     * Отримання всіх збережених логів
     */
    fun getStoredLogs(): List<String> {
        val logs = mutableListOf<String>()
        logDir.listFiles()?.sortedBy { it.lastModified() }?.forEach { file ->
            try {
                FileReader(file).use { reader ->
                    logs.add(reader.readText())
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to read log file: ${file.name}")
            }
        }
        return logs
    }
    
    /**
     * Очищення всіх логів
     */
    fun clearLogs() {
        logDir.listFiles()?.forEach { it.delete() }
    }
    
    private fun getCurrentLogFile(): File {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return File(logDir, "log_$date.txt")
    }
    
    private fun rotateLogs() {
        val files = logDir.listFiles()?.sortedBy { it.lastModified() } ?: return
        if (files.size >= MAX_LOG_FILES) {
            files.first().delete()
        }
    }
    
    private fun cleanupOldLogs() {
        val files = logDir.listFiles()?.sortedBy { it.lastModified() } ?: return
        if (files.size > MAX_LOG_FILES) {
            files.take(files.size - MAX_LOG_FILES).forEach { it.delete() }
        }
    }
    
    private fun getLevelString(level: Int): String = when (level) {
        Log.VERBOSE -> "V"
        Log.DEBUG -> "D"
        Log.INFO -> "I"
        Log.WARN -> "W"
        Log.ERROR -> "E"
        Log.ASSERT -> "A"
        else -> "?"
    }
} 