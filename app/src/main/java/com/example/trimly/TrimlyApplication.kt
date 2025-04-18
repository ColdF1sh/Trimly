package com.example.trimly

import android.app.Application
import com.example.trimly.utils.Logging

class TrimlyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Ініціалізуємо логування
        // В режимі відладки (BuildConfig.DEBUG == true) буде використовуватися DebugTree
        // В продакшн режимі - CrashReportingTree
        Logging.init(this, BuildConfig.DEBUG)
        
        Timber.i("Application started")
    }
} 