package com.bagaspardanailham.myecommerceapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class GetProductDetailResponse(

	@field:SerializedName("success")
	val success: GetProductDetailSuccess? = null
)

data class GetProductDetailSuccess(

	@field:SerializedName("data")
	val data: ProductDetailItem? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class ImageProductItem(

	@field:SerializedName("image_product")
	val imageProduct: String? = null,

	@field:SerializedName("title_product")
	val titleProduct: String? = null
)

data class ProductDetailItem(

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

	@field:SerializedName("image_product")
	val imageProduct: List<ImageProductItem?>? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("stock")
	val stock: Int? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("desc")
	val desc: String? = null,

	@field:SerializedName("isFavorite")
	val isFavorite: Boolean
)
