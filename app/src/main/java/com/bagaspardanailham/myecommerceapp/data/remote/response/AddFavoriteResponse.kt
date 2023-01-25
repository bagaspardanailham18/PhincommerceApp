package com.bagaspardanailham.myecommerceapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class AddFavoriteResponse(

	@field:SerializedName("success")
	val success: AddFavoriteSuccess? = null
)

data class AddFavoriteSuccess(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)
