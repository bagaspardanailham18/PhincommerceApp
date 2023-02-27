package com.bagaspardanailham.myecommerceapp.data.remote.response.profile

import com.google.gson.annotations.SerializedName

//data class ErrorChangeImageResponse(
//
//	@field:SerializedName("error")
//	val error: Error? = null
//)

data class Message(

	@field:SerializedName("image")
	val image: List<String?>? = null
)

data class Error(

	@field:SerializedName("message")
	val message: Message? = null,

	@field:SerializedName("status")
	val status: Int? = null
)
