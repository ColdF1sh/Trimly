package com.example.trimly.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import timber.log.Timber

object DatabaseUtils {
    private const val DB_NAME = "SalonApp.db"

    fun copyDatabaseFromAssets(context: Context) {
        val dbPath = context.getDatabasePath(DB_NAME)
        Timber.i("Database path: ${dbPath.absolutePath}")
        Timber.i("Database exists: ${dbPath.exists()}")

        dbPath.parentFile?.mkdirs()
        try {
            Timber.i("Opening database from assets")
            val inputStream: InputStream = context.assets.open(DB_NAME)
            val outputStream: OutputStream = FileOutputStream(dbPath)
            val buffer = ByteArray(1024)
            var length: Int
            var totalBytes = 0L
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
                totalBytes += length
            }
            outputStream.flush()
            outputStream.close()
            inputStream.close()
            Timber.i("Database copied successfully. Total bytes: $totalBytes")
        } catch (e: IOException) {
            Timber.e(e, "Error copying database from assets")
            throw e
        }
    }

    fun openDatabase(context: Context): SQLiteDatabase {
        val dbPath = context.getDatabasePath(DB_NAME).path
        Timber.i("Opening database at path: $dbPath")
        return SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)
    }
} 