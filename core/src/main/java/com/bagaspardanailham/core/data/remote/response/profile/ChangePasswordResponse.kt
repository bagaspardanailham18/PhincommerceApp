package com.bagaspardanailham.core.data.remote.response.profile

import com.bagaspardanailham.core.data.remote.response.SuccessResponse
import com.google.gson.annotations.SerializedName

data class ChangePasswordResponse(
	@field:SerializedName("success")
	val success: SuccessResponse? = null
)