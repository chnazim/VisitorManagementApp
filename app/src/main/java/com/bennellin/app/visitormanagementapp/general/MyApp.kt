package com.bennellin.app.visitormanagementapp.general

import android.app.Application
import android.content.Context

class MyApp : Application() {

    companion object {
        private var instance: MyApp? = null

        fun getAppContext(): Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        SharedPreferenceManager.init(this)
        instance = this
    }


}