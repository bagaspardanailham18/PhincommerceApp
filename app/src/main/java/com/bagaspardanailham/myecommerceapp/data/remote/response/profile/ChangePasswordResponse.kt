package com.bagaspardanailham.myecommerceapp.data.remote.response.profile

import com.bagaspardanailham.myecommerceapp.data.remote.response.SuccessResponse
import com.google.gson.annotations.SerializedName

data class ChangePasswordResponse(
	@field:SerializedName("success")
	val success: SuccessResponse? = null
)