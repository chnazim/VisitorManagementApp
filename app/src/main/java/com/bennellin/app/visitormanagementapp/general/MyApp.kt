package com.bennellin.app.visitormanagementapp.general

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import android.preference.PreferenceManager
import com.bennellin.app.visitormanagementapp.Logger.Logger

class MyApp : Application() {

    companion object {
        private lateinit var context: Context

        fun getContext(): Context = context.applicationContext

        var VG_URL: String = ""
        var isReading: Boolean = false
        var IN_PROCESS: Boolean = true
        var path: String = ""
    }

    override fun onCreate() {
        super.onCreate()
        SharedPreferenceManager.init(this)
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val url = sharedPreferences.getString("VG_URL", "https://101.53.158.186/VGPreProd/ValidationGateway")
        VG_URL = url?.trim() ?: ""
        IN_PROCESS = sharedPreferences.getBoolean("IN_PROCESS", true)
        Logger.d("VG_URL__${VG_URL}")
        path = "${Environment.getExternalStorageDirectory().absolutePath}/VisitorManagementApp/"
        context = this

    }


}