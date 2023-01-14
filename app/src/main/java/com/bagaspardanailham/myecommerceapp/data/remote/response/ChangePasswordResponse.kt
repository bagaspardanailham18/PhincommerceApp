package com.bagaspardanailham.myecommerceapp.data.remote.response

import com.bagaspardanailham.myecommerceapp.data.Result
import com.google.gson.annotations.SerializedName

data class ChangePasswordResponse(

	@field:SerializedName("success")
	val success: ChangePasswordSuccess? = null
)

data class ChangePasswordSuccess(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)
