package com.bagaspardanailham.myecommerceapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.bagaspardanailham.core.data.RoomResult
import com.bagaspardanailham.core.data.remote.response.SuccessResponse
import com.bagaspardanailham.core.data.remote.response.auth.DataUser
import com.bagaspardanailham.core.data.remote.response.auth.LoginResponse
import com.bagaspardanailham.core.data.remote.response.auth.LoginSuccess
import com.bagaspardanailham.core.data.remote.response.auth.RegisterResponse
import com.bagaspardanailham.core.data.remote.response.product.*
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
        val response = SuccessResponse("", null)

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
        for (i in 0..10) {
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

    fun generateProductDetailResponse(): GetProductDetailResponse {
        val data = ProductDetailItem(
            date = "date",
            image = "image",
            nameProduct = "nameProduct",
            harga = "harga",
            size = "size",
            rate = 0,
            weight = "weight",
            imageProduct = listOf(ImageProductItem("image", "title")),
            id = 0,
            stock = 0,
            type = "type",
            desc = "desc",
            isFavorite = false
        )

        return GetProductDetailResponse(
            success = GetProductDetailSuccess(
                data, "message", 0
            )
        )
    }

    fun generateAddProductToFavoriteResponse(): AddFavoriteResponse {
        return AddFavoriteResponse(
            SuccessResponse("message", null)
        )
    }

    fun generateRemoveProductFromFavoriteResponse(): RemoveFavoriteResponse {
        return RemoveFavoriteResponse(
            SuccessResponse("message", null)
        )
    }

    fun generateUpdateStockResponse(): UpdateStockResponse {
        return UpdateStockResponse(
            SuccessResponse("message", null)
        )
    }

    fun generateUpdateRateResponse(): UpdateRateResponse {
        return UpdateRateResponse(
            SuccessResponse("message", null)
        )
    }

    fun generateProductSearchHistory(): GetProductSearchHistoryResponse {
        return GetProductSearchHistoryResponse(
            GetProductSearchHistorySuccess(
                data = listOf(
                    ProductListItem(
                        0, "date", "image", "nameProduct", "harga","size", 0, "weight", 0, "type", "desc"
                )),
                message = "message",
                status = null
            )
        )
    }

    fun generateOtherProductList(): GetOtherProductListResponse {
        return GetOtherProductListResponse(
            GetOtherProductSuccess(
                data = listOf(
                    ProductListItem(
                        0, "date", "image", "nameProduct", "harga","size", 0, "weight", 0, "type", "desc"
                    )
                ),
                message = "message",
                status = null
            )
        )
    }

    fun generateProductPagingList(): GetProductListPagingResponse {
        return GetProductListPagingResponse(
            GetProductListPagingSuccess(
                1,
                data = listOf(ProductListPagingItem(
                    "date", "image", "nameProduct", "price", "size", 1, "weight", 1, 1, "type", "desc"
                )),
                message = "message",
                status = null
            )
        )
    }
}