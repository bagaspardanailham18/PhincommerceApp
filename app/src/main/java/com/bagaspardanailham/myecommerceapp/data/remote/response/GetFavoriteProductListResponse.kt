package com.bagaspardanailham.myecommerceapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class GetFavoriteProductListResponse(

	@field:SerializedName("success")
	val success: GetFavoriteProductListSuccess? = null
)

data class GetFavoriteProductListSuccess(

	@field:SerializedName("data")
	val data: List<FavoriteProductItem?>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class FavoriteProductItem(

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

	@field:SerializedName("stock")
	val stock: Int? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("desc")
	val desc: String? = null
)
