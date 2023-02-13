package com.bagaspardanailham.myecommerceapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class GetProductSearchHistoryResponse(

	@field:SerializedName("success")
	val success: GetProductSearchHistorySuccess? = null
)

data class GetProductSearchHistorySuccess(

	@field:SerializedName("data")
	val data: List<ProductListItem?>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)