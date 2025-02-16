package com.bennellin.app.visitormanagementapp.tab.network.models

data class FilterApiRequestBody(
    val From: String,
    val To: String,
    val FilterFeild: String,
    val DataToSearch: String
)
