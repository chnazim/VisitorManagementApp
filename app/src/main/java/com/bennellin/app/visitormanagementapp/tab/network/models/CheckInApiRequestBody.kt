package com.bennellin.app.visitormanagementapp.tab.network.models

data class CheckInApiRequestBody(
    val NameEnglish: String,
    val NameArabic: String,
    val IdNumber: String,
    val IdCardNumber: String,
    val IssueDate: String,
    val ExpiryDate: String,
    val DateOfBirth: String,
    val NationalityCode: String,
    val Email: String,
    val Phone: String,
    val RawData: String,
    val ProfilePictureBase64: String,
    val VisitorType: String,
    val VisitPurpose: String,
    val IdAccessCard: String,
    val Remarks: String
)
