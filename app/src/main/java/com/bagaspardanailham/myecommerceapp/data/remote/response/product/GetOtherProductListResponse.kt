package com.bagaspardanailham.myecommerceapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class GetOtherProductListResponse(

	@field:SerializedName("success")
	val success: GetOtherProductSuccess? = null
)

data class GetOtherProductSuccess(

	@field:SerializedName("data")
	val data: List<ProductListItem?>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)
