package com.bennellin.app.visitormanagementapp.tab.network

import com.bennellin.app.visitormanagementapp.general.utils
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(utils.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

}