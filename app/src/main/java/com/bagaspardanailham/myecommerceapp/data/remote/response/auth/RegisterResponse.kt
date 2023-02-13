package com.bagaspardanailham.myecommerceapp.data.remote.response.auth

import com.bagaspardanailham.myecommerceapp.data.remote.response.SuccessResponse
import com.google.gson.annotations.SerializedName

data class RegisterResponse(

	@field:SerializedName("success")
	val success: SuccessResponse? = null,

	)
