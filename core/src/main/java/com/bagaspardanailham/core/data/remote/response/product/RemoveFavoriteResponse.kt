package com.bagaspardanailham.core.data.remote.response.product

import com.bagaspardanailham.core.data.remote.response.SuccessResponse
import com.google.gson.annotations.SerializedName

data class RemoveFavoriteResponse(

	@field:SerializedName("success")
	val success: SuccessResponse? = null
)
