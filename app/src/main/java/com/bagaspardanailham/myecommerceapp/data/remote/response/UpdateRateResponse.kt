package com.bagaspardanailham.myecommerceapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class UpdateRateResponse(

	@field:SerializedName("success")
	val success: UpdateRateSuccess? = null
)

data class UpdateRateSuccess(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)
