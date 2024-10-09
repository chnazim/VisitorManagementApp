package com.bennellin.app.visitormanagementapp.general

import android.content.Context
import android.content.SharedPreferences

object SharedPreferenceManager {

    private const val PREFS_NAME = "app_preferences"
    private lateinit var preferences: SharedPreferences


    // Initialize the SharedPreferences
    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveLoggedIn(key: String, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }

    fun isLoggedIn(key: String, defaultValue: Boolean = false): Boolean {
        return preferences.getBoolean(key, defaultValue)
    }

    fun saveAuthToken(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    fun getAuthToken(key: String): String? {
        return preferences.getString(key, null)
    }

    fun saveUserId(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    fun getUserId(key: String): String? {
        return preferences.getString(key, null)
    }

    fun saveUserName(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    fun getUserName(key: String): String? {
        return preferences.getString(key, null)
    }


}