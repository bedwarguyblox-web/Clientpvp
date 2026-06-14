package com.dsmp.pvpclient

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class annotated with @HiltAndroidApp so Hilt can generate
 * the component hierarchy and inject dependencies throughout the app.
 */
@HiltAndroidApp
class DSMPApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
