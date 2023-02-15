package com.bagaspardanailham.myecommerceapp.ui.payment

import com.google.gson.annotations.SerializedName

sealed class DataModel {
    data class Header(
        val id: String? = null,
        val type: String? = null,
        val order: Int? = null
    ) : DataModel()
    data class VirtualAccount(
        val name: String? = null,
        val id: String? = null,
        val order: Int? = null,
        val status: Boolean? = null
    ) : DataModel()
    data class EWallet(
        val name: String? = null,
        val id: String? = null,
        val order: Int? = null,
        val status: Boolean? = null
    ) : DataModel()
}
