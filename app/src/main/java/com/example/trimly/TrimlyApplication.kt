package com.example.trimly

import android.app.Application
import com.example.trimly.utils.Logging
import timber.log.Timber

/**
 * Головний клас додатку.
 * 
 * Ініціалізує основні компоненти та налаштування.
 */
class TrimlyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize logging
        Logging.init(this, BuildConfig.DEBUG)
        
        // Log application start
        Timber.i("Application started")
    }
} 