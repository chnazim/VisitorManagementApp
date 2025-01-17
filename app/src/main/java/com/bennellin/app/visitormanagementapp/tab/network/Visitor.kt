package com.bennellin.app.visitormanagementapp.tab.network

import com.google.gson.annotations.SerializedName

data class Visitor(
    @SerializedName("IdRegisteredPerson") val visitorID: String,
    @SerializedName("NameEnglish") val name: String,
    @SerializedName("CreatedOn") val entryTime: String,
    @SerializedName("ExitTime") val exitTime: String,
    @SerializedName("IdNumber") val idNumber: String,
    @SerializedName("VisitPurpose") val visitPurpose: String,
    val remark: String,
    @SerializedName("Phone") val contactNumber: String,
    val companyName: String,
    @SerializedName("IdAccessCard") val accessCardNumber: String,
    val registrationStatus: String
)
