package com.bagaspardanailham.myecommerceapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class RemoveFavoriteResponse(

	@field:SerializedName("success")
	val success: RemoveFavoriteSuccess? = null
)

data class RemoveFavoriteSuccess(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)
