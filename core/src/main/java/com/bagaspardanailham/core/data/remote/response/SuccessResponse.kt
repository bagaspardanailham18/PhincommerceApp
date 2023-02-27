package com.bagaspardanailham.core.data.remote.response

import com.google.gson.annotations.SerializedName

data class SuccessResponse(
    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: Int? = null
)
