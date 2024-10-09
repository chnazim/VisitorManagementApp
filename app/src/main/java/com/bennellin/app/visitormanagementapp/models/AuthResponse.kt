package com.bennellin.app.visitormanagementapp.models

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Int,
    val userName: String,
    @SerializedName(".issued") val issued: String,
    @SerializedName(".expires") val expires: String,
    val user_id: String
)
