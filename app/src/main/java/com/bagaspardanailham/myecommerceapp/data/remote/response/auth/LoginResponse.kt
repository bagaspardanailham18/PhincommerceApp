package com.bagaspardanailham.myecommerceapp.data.remote.response.auth

import com.google.gson.annotations.SerializedName

data class LoginResponse(

	@field:SerializedName("success")
	val success: LoginSuccess? = null,

	)

data class LoginSuccess(

	@field:SerializedName("access_token")
	val accessToken: String? = null,

	@field:SerializedName("refresh_token")
	val refreshToken: String? = null,

	@field:SerializedName("data_user")
	val dataUser: DataUser? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class DataUser(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("path")
	val path: String? = null,

	@field:SerializedName("gender")
	val gender: Int? = null,

	@field:SerializedName("phone")
	val phone: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("email")
	val email: String? = null
)
