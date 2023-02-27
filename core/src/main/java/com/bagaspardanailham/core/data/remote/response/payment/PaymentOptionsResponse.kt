package com.bagaspardanailham.core.data.remote.response.payment

import com.google.gson.annotations.SerializedName

data class PaymentOptionsResponse(

	@field:SerializedName("PaymentOptionsResponse")
	val paymentOptionsResponse: List<PaymentTypeOptionsItem?>? = null
)

data class PaymentOptionsDataItem(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("order")
	val order: Int? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)

data class PaymentTypeOptionsItem(

	@field:SerializedName("data")
	val data: List<PaymentOptionsDataItem?>? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("order")
	val order: Int? = null
)
