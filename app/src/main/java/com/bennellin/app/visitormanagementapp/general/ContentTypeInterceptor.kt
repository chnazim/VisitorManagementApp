package com.bennellin.app.visitormanagementapp.general

import okhttp3.Interceptor
import okhttp3.Response

class ContentTypeInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestWithHeaders = originalRequest.newBuilder()
            .header("Content-Type", "application/json") // Set Content-Type header
            .build()
        return chain.proceed(requestWithHeaders)
    }
}