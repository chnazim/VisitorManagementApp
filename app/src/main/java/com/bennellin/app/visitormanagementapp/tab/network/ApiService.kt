package com.bennellin.app.visitormanagementapp.tab.network

import com.bennellin.app.visitormanagementapp.models.AuthResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("/token")
    fun authenticate(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("grant_type") grantType: String = "password"
    ): Call<AuthResponse>

    @GET("/api/Values/GetLastNVisitors?")
    fun getNVisilorsList(
        @Header("Authorization") authToken: String,
        @Query("limit") limit: Int
    ):Call<List<Visitor>>
}