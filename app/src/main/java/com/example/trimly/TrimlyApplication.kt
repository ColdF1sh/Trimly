package com.example.trimly

import android.app.Application
import com.example.trimly.utils.DatabaseUtils
import com.example.trimly.utils.Logging
import timber.log.Timber

class TrimlyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize logging first
        Logging.init(this, BuildConfig.DEBUG)

        // Log application start
        Timber.i("Application started")

        // Copy SalonApp.db from assets if needed
        Timber.i("Starting database copy from assets")
        try {
            DatabaseUtils.copyDatabaseFromAssets(this)
            Timber.i("Database copied successfully")
        } catch (e: Exception) {
            Timber.e(e, "Error copying database from assets")
        }
    }
}
