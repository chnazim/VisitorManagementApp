package com.bennellin.app.visitormanagementapp.tab.network

import com.bennellin.app.visitormanagementapp.models.AuthResponse
import com.bennellin.app.visitormanagementapp.tab.network.models.CheckInApiRequestBody
import com.bennellin.app.visitormanagementapp.tab.network.models.FilterApiRequestBody
import com.bennellin.app.visitormanagementapp.tab.network.models.VisitPurpose
import com.bennellin.app.visitormanagementapp.tab.network.models.Visitor
import com.bennellin.app.visitormanagementapp.tab.network.models.VisitorType
import retrofit2.Call
import retrofit2.http.Body
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
    ): Call<List<Visitor>>

    @GET("/api/Values/GetVisitorTypes?")
    fun getVisitorType(
        @Header("Authorization") authToken: String,
    ): Call<List<VisitorType>>

    @GET("/api/Values/GetVisitPurpose?")
    fun getVisitorPurpose(
        @Header("Authorization") authToken: String,
    ): Call<List<VisitPurpose>>

    @POST("/api/Values/InsertEIDData")
    fun checkInRequest(
        @Header("Authorization") authToken: String,
        @Body requestBody: CheckInApiRequestBody
    ): Call<String>

    @GET("/api/Values/MarkCheckoutTime?")
    fun checkOutVisitor(
        @Header("Authorization") authToken: String,
        @Query("IDEidReadings") idEidReadings: Int
    ): Call<String>

    @GET("/api/Values/GetLastNVisitsOfAPerson?")
    fun getVisitHistory(
        @Header("Authorization") authToken: String,
        @Query("IdRegisteredPerson") visitorId: String
    ): Call<List<Visitor>>

    @POST("/api/Values/SearchVisitorHistory")
    fun filteredVisitors(
        @Header("Authorization") authToken: String,
        @Body requestBody: FilterApiRequestBody
    ): Call<List<Visitor>>
}