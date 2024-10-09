package com.bennellin.app.visitormanagementapp.general

import android.os.Environment

class utils {

    companion object {
        @kotlin.jvm.JvmField
        var VGL_URL: String = "http://vg-pre-prod.ica.gov.ae/ValidationGatewayService"

        @kotlin.jvm.JvmField
        var path = Environment.getExternalStorageDirectory().absolutePath + "/EIDAToolkit/"
    }
}