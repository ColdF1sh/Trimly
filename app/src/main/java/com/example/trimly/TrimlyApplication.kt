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
        
        // Ініціалізація логування
        Logging.init(BuildConfig.DEBUG)
        
        // Налаштування логування
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
} 