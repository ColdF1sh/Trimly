package com.example.trimly.utils

import android.content.Context
import android.os.Environment
import com.example.trimly.utils.Logging
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Клас для зберігання логів додатку.
 * 
 * Зберігає логи у файловій системі та надає методи для їх отримання.
 * 
 * @property context Контекст додатку
 */
class LogStorage(private val context: Context) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val logDir: File
        get() = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "logs")

    init {
        if (!logDir.exists()) {
            logDir.mkdirs()
        }
    }

    /**
     * Отримує файл логу для поточної дати.
     * 
     * @return Файл логу
     */
    private fun getLogFile(): File {
        val fileName = "log_${dateFormat.format(Date())}.txt"
        return File(logDir, fileName)
    }

    /**
     * Додає запис до логу.
     * 
     * @param message Повідомлення для логування
     */
    fun appendLog(message: String) {
        try {
            val logFile = getLogFile()
            val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            val logMessage = "[$timestamp] $message\n"
            
            logFile.appendText(logMessage)
            Logging.d("Log appended: $message")
        } catch (e: Exception) {
            Logging.e("Failed to append log", e)
        }
    }

    /**
     * Отримує всі логи за вказану дату.
     * 
     * @param date Дата логів
     * @return Список записів логу
     */
    fun getLogs(date: Date): List<String> {
        val fileName = "log_${dateFormat.format(date)}.txt"
        val file = File(logDir, fileName)
        
        return if (file.exists()) {
            try {
                file.readLines()
            } catch (e: Exception) {
                Logging.e("Failed to read logs", e)
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    /**
     * Очищає старі логи.
     * 
     * @param daysToKeep Кількість днів, за які зберігати логи
     */
    fun cleanupOldLogs(daysToKeep: Int) {
        try {
            val cutoffDate = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -daysToKeep)
            }.time

            logDir.listFiles()?.forEach { file ->
                val fileName = file.name
                if (fileName.startsWith("log_")) {
                    val dateStr = fileName.substring(4, 14)
                    val fileDate = dateFormat.parse(dateStr)
                    
                    if (fileDate != null && fileDate.before(cutoffDate)) {
                        if (file.delete()) {
                            Logging.d("Deleted old log file: ${file.name}")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Logging.e("Failed to cleanup old logs", e)
        }
    }
} 