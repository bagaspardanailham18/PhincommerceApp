package com.bagaspardanailham.myecommerceapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class ChangeImageResponse(

	@field:SerializedName("success")
	val success: Success
)

data class Success(

	@field:SerializedName("path")
	val path: String,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Int
)
