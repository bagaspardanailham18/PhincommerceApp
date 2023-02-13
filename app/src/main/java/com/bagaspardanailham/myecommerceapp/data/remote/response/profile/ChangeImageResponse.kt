package com.bagaspardanailham.myecommerceapp.data.remote.response.profile

import com.google.gson.annotations.SerializedName

data class ChangeImageResponse(

	@field:SerializedName("success")
	val success: ChangeImageSuccess
)

data class ChangeImageSuccess(

	@field:SerializedName("path")
	val path: String,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Int
)
