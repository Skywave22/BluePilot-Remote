package com.bluepilot.remote

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for BluePilot Remote
 * Initializes Hilt dependency injection
 */
@HiltAndroidApp
class BluePilotApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
    }
}
