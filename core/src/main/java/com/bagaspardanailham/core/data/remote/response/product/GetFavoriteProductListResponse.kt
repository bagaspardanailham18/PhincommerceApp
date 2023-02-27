package com.bagaspardanailham.core.data.remote.response.product

import com.google.gson.annotations.SerializedName

data class GetFavoriteProductListResponse(

	@field:SerializedName("success")
	val success: GetFavoriteProductListSuccess? = null
)

data class GetFavoriteProductListSuccess(

	@field:SerializedName("data")
	val data: List<ProductListItem?>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)
