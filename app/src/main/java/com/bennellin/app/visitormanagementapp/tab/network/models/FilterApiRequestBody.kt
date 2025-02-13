package com.bennellin.app.visitormanagementapp.tab.network.models

data class FilterApiRequestBody(
    val FromDate: String,
    val ToDate: String,
    val VisitPurpose: String,
    val VisitorType: String,
    val Email: String,
    val Phone: String,
    val Name: String,
    val IdNumber: String
)
