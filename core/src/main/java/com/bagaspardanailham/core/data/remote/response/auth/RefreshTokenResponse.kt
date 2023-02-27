package com.bagaspardanailham.core.data.remote.response.auth

import com.google.gson.annotations.SerializedName

data class RefreshTokenResponse(

	@field:SerializedName("success")
	val success: RefreshTokenSuccess? = null
)

data class RefreshTokenSuccess(

	@field:SerializedName("access_token")
	val accessToken: String? = null,

	@field:SerializedName("refresh_token")
	val refreshToken: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)
