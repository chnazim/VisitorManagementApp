package com.bennellin.app.visitormanagementapp.general

import com.bennellin.app.visitormanagementapp.models.AuthRequest
import com.bennellin.app.visitormanagementapp.models.AuthResponse
import com.bennellin.app.visitormanagementapp.models.GateModel
import com.bennellin.app.visitormanagementapp.models.VisitorRequest
import com.bennellin.app.visitormanagementapp.models.VisitorResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("/Token")
    fun authenticateUser(
        @Body authRequest: AuthRequest
    ): Call<AuthResponse>

    @Headers("Content-Type: application/json")
    @POST("/api/visitor/log_visitor_entry")
    fun addVisitor(
        @Body visitorInfo: VisitorRequest
    ): Call<VisitorResponse>

    @Headers("Content-Type: application/json")
    @GET("/api/visitor/get_all_gates")
    fun getAllGates(): Call<List<GateModel>>
}