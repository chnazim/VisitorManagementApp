package com.bennellin.app.visitormanagementapp.models

import java.sql.Date

data class VisitorRequest(
    val IDNumber: String,
    val IssueDate: Date,
    val ExpiryDate: Date,
    val FullNameArabic: String,
    val FullNameEnglish: String,
    val Gender: String,
    val NationalityEnglish: String,
    val DateOfBirth: Date,
    val PlaceOfBirthEnglish: String,
    val ResidencyExpiryDate: Date,
    val EmiratesDescEnglish: String,
    val StreetEnglish: String,
    val AreaDescEnglish: String,
    val MobilePhoneNumber: String,
    val EmailID: String,
    val CardHolderPhoto: String,
    val IdGate: Int,
    val IdUser: String,
    val NumberOfBags: Int,
    val Shelves: String,
    val BaggageTags: String


)
