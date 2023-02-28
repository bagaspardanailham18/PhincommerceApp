package com.bagaspardanailham.core

import com.bagaspardanailham.core.data.DataStock
import com.bagaspardanailham.core.data.remote.ApiService
import com.bagaspardanailham.core.data.remote.response.auth.LoginResponse
import com.bagaspardanailham.core.data.remote.response.auth.RefreshTokenResponse
import com.bagaspardanailham.core.data.remote.response.auth.RegisterResponse
import com.bagaspardanailham.core.data.remote.response.product.*
import com.bagaspardanailham.core.data.remote.response.profile.ChangeImageResponse
import com.bagaspardanailham.core.data.remote.response.profile.ChangePasswordResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class FakeApiService : ApiService {

    private val dummyLoginResponse = DataDummy.generateDummyLoginResponse()
    private val dummyRegisterResponse = DataDummy.generateDummyRegisterResponse()

    override suspend fun loginUser(
        apikey: String,
        email: String,
        password: String,
        tokenFcm: String
    ): LoginResponse {
        return dummyLoginResponse
    }

    override suspend fun registerUser(
        apikey: String,
        name: RequestBody?,
        email: RequestBody?,
        password: RequestBody?,
        phone: RequestBody?,
        gender: RequestBody?,
        image: MultipartBody.Part?
    ): RegisterResponse {
        return dummyRegisterResponse
    }

    override suspend fun changePassword(
        apikey: String,
        token: String,
        id: Int,
        password: String,
        new_password: String,
        confirm_password: String
    ): ChangePasswordResponse {
        TODO("Not yet implemented")
    }

    override suspend fun changeImage(
        apikey: String,
        token: String,
        id: RequestBody,
        image: MultipartBody.Part
    ): ChangeImageResponse {
        TODO("Not yet implemented")
    }

    override suspend fun refreshToken(
        apikey: String,
        idUser: Int,
        accessToken: String,
        refreshToken: String
    ): Response<RefreshTokenResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getProductList(
        apikey: String,
        token: String,
        search: String?
    ): GetProductListResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getFavoriteProductList(
        apikey: String,
        token: String,
        search: String?,
        iduser: Int
    ): GetFavoriteProductListResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getProductDetail(
        apikey: String,
        token: String,
        idproduct: Int?,
        isuser: Int?
    ): GetProductDetailResponse {
        TODO("Not yet implemented")
    }

    override suspend fun addFavoriteProduct(
        apikey: String,
        token: String,
        idproduct: Int?,
        iduser: Int?
    ): AddFavoriteResponse {
        TODO("Not yet implemented")
    }

    override suspend fun removeFavoriteProduct(
        apikey: String,
        token: String,
        idproduct: Int?,
        iduser: Int?
    ): RemoveFavoriteResponse {
        TODO("Not yet implemented")
    }

    override suspend fun updateStock(
        apikey: String,
        token: String,
        dataStock: DataStock
    ): UpdateStockResponse {
        TODO("Not yet implemented")
    }

    override suspend fun updateRate(
        apikey: String,
        token: String,
        idproduct: Int?,
        rate: String
    ): UpdateRateResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getProductListPaging(
        search: String?,
        offset: Int
    ): GetProductListPagingResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getOtherProducts(iduser: Int?): GetOtherProductListResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getProductSearchHistory(iduser: Int?): GetProductSearchHistoryResponse {
        TODO("Not yet implemented")
    }
}