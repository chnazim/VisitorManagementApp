package com.bennellin.app.visitormanagementapp.general

import android.app.Application

class VisitorManagementApp : Application() {
    override fun onCreate() {
        super.onCreate()
        SharedPreferenceManager.init(this) // Initialize SharedPreferences
    }
}