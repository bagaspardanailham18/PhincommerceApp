package com.bagaspardanailham.core.data.remote.response.auth

import com.bagaspardanailham.core.data.remote.response.SuccessResponse
import com.google.gson.annotations.SerializedName

data class RegisterResponse(

	@field:SerializedName("success")
	val success: SuccessResponse? = null,

	)
