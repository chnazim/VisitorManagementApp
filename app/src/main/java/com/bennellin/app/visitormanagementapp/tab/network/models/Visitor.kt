package com.bennellin.app.visitormanagementapp.tab.network.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Visitor(
    @SerializedName("IDEidReadings") val idEidReading: Int,
    @SerializedName("IdRegisteredPerson") val visitorID: String,
    @SerializedName("NameEnglish") val name: String,
    @SerializedName("CreatedOn") val entryTime: String,
    @SerializedName("ExitTime") val exitTime: String,
    @SerializedName("IdNumber") val idNumber: String,
    @SerializedName("VisitPurpose") val visitPurpose: String,
    @SerializedName("Remarks") val remark: String,
    @SerializedName("Phone") val contactNumber: String,
    val companyName: String,
    @SerializedName("IdAccessCard") val accessCardNumber: String,
    val registrationStatus: String,
    val Email: String,
    @SerializedName("ProfilePictureBase64") val profilePicture: String,
) : Serializable
