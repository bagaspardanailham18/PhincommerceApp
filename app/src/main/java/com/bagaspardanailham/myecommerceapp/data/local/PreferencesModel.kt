package com.bagaspardanailham.myecommerceapp.data.local

data class PreferencesModel(
    val authTokenKey: String,
    val refreshTokenKey: String,
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val gender: String,
    val imgPath: String
)
