package com.bennellin.app.visitormanagementapp.general

import android.os.Environment

class utils {

    companion object {
        @kotlin.jvm.JvmField
//        var VGL_URL: String = "http://vg-pre-prod.ica.gov.ae/ValidationGatewayService"
        var VGL_URL: String = "https://101.53.158.186/VGPreProd/ValidationGateway"

        @kotlin.jvm.JvmField
        var path = Environment.getExternalStorageDirectory().absolutePath + "/EIDAToolkit/"

        @kotlin.jvm.JvmField
        var BASE_URL: String = "http://visitorms.zapto.org:5580"

//        @kotlin.jvm.JvmField
//        var BASE_URL :String = "http://92.99.170.66:5580"

        @kotlin.jvm.JvmField
        var LIMIT: Int = 100
    }
}