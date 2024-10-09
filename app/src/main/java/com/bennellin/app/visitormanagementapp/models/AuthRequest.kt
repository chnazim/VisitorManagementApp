package com.bennellin.app.visitormanagementapp.models

import com.google.gson.annotations.SerializedName

data class AuthRequest(
    val userName: String,
    val password: String,
    @SerializedName("grant_type") val grandTpe: String
)
