package com.bagaspardanailham.myecommerceapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class GetProductListPagingResponse(

	@field:SerializedName("success")
	val success: GetProductListPagingSuccess? = null
)

data class ProductListPagingItem(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("name_product")
	val nameProduct: String? = null,

	@field:SerializedName("harga")
	val harga: String? = null,

	@field:SerializedName("size")
	val size: String? = null,

	@field:SerializedName("rate")
	val rate: Int? = null,

	@field:SerializedName("weight")
	val weight: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("stock")
	val stock: Int? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("desc")
	val desc: String? = null
)

data class GetProductListPagingSuccess(

	@field:SerializedName("total_row")
	val totalRow: Int? = null,

	@field:SerializedName("data")
	val data: List<ProductListPagingItem>,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)
