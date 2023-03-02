package com.bagaspardanailham.myecommerceapp

import com.bagaspardanailham.core.data.remote.response.SuccessResponse
import com.bagaspardanailham.core.data.remote.response.auth.DataUser
import com.bagaspardanailham.core.data.remote.response.auth.LoginResponse
import com.bagaspardanailham.core.data.remote.response.auth.LoginSuccess
import com.bagaspardanailham.core.data.remote.response.auth.RegisterResponse
import com.bagaspardanailham.core.data.remote.response.product.GetFavoriteProductListResponse
import com.bagaspardanailham.core.data.remote.response.product.GetFavoriteProductListSuccess
import com.bagaspardanailham.core.data.remote.response.product.ProductListItem
import com.bagaspardanailham.core.data.remote.response.product.ProductListPagingItem
import com.bagaspardanailham.core.data.remote.response.profile.ChangeImageResponse
import com.bagaspardanailham.core.data.remote.response.profile.ChangeImageSuccess
import com.bagaspardanailham.core.data.remote.response.profile.ChangePasswordResponse

object DataDummy {

    fun generateDummyLoginResponse(): LoginResponse {
        return LoginResponse(
            LoginSuccess(
                accessToken = "accessToken",
                refreshToken = "refreshToken",
                dataUser = generateDummyDataUser(),
                message = "message",
                status = null,
            )
        )
    }

    private fun generateDummyDataUser(): DataUser {
        return DataUser(
            image = "image",
            path = "path",
            gender = 1,
            phone = "phone",
            name = "name",
            id = 1,
            email = "email"
        )
    }

    fun generateDummyRegisterResponse(): RegisterResponse {
        val response = SuccessResponse("message", 200)

        return RegisterResponse(
            success = response
        )
    }

    fun generateDummyChangePasswordResponse(): ChangePasswordResponse {
        val respone = SuccessResponse("message", null)

        return ChangePasswordResponse(
            success = respone
        )
    }

    fun generateDummyChangeImageResponse(): ChangeImageResponse {
        val response = ChangeImageSuccess("path", "message", 200)
        return ChangeImageResponse(
            success = response
        )
    }

    fun generateDummyProductListPaging(): List<ProductListPagingItem> {
        val items: MutableList<ProductListPagingItem> = arrayListOf()
        for (i in 0..100) {
            val product = ProductListPagingItem(
                "date",
                "image",
                "name",
                "harga",
                "size",
                0,
                "weight",
                0,
                0,
                "type",
                "desc"
            )
            items.add(product)
        }
        return items
    }

    fun generateDummyFavoriteProductListResponse(): GetFavoriteProductListResponse {
        val items: MutableList<ProductListItem> = arrayListOf()
        for (i in 0..10) {
            val product = ProductListItem(
                0,
                "date",
                "image",
                "nameProduct",
                "price",
                "size",
                0,
                "weight",
                0,
                "type",
                "desc"
            )
            items.add(product)
        }

        return GetFavoriteProductListResponse(
            GetFavoriteProductListSuccess(
                data = items,
                message = "message",
                status = null
            )
        )
    }

}