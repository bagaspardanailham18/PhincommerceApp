package com.bagaspardanailham.core.data.remote

import com.bagaspardanailham.core.data.DataStock
import com.bagaspardanailham.core.data.remote.response.*
import com.bagaspardanailham.core.data.remote.response.auth.LoginResponse
import com.bagaspardanailham.core.data.remote.response.auth.RefreshTokenResponse
import com.bagaspardanailham.core.data.remote.response.auth.RegisterResponse
import com.bagaspardanailham.core.data.remote.response.profile.ChangeImageResponse
import com.bagaspardanailham.core.data.remote.response.profile.ChangePasswordResponse
import com.bagaspardanailham.core.data.remote.response.product.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("api/ecommerce/authentication")
    suspend fun loginUser(
        @Header("apikey") apikey: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("token_fcm") tokenFcm: String
    ) : LoginResponse

    @Multipart
    @POST("api/ecommerce/registration")
    suspend fun registerUser(
        @Header("apikey") apikey: String,
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part image: MultipartBody.Part,
    ) : RegisterResponse

    @FormUrlEncoded
    @PUT("api/ecommerce/change-password/{id}")
    suspend fun changePassword(
        @Header("apikey") apikey: String,
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Field("password") password: String,
        @Field("new_password") new_password: String,
        @Field("confirm_password") confirm_password: String
    ) : ChangePasswordResponse

    @Multipart
    @POST("api/ecommerce/change-image")
    suspend fun changeImage(
        @Header("apikey") apikey: String,
        @Header("Authorization") token: String,
        @Part("id") id: RequestBody,
        @Part image: MultipartBody.Part
    ) : ChangeImageResponse

    @FormUrlEncoded
    @POST("api/ecommerce/refresh-token")
    suspend fun refreshToken(
        @Header("apikey") apikey: String,
        @Field("id_user") idUser: Int,
        @Field("access_token") accessToken: String,
        @Field("refresh_token") refreshToken: String
    ) : retrofit2.Response<RefreshTokenResponse>

    @GET("api/ecommerce/get_list_product")
    suspend fun getProductList(
        @Header("apikey") apikey: String,
        @Header("Authorization") token: String,
        @Query("search") search: String?
    ) : GetProductListResponse

    @GET("api/ecommerce/get_list_product_favorite")
    suspend fun getFavoriteProductList(
        @Header("apikey") apikey: String,
        @Header("Authorization") token: String,
        @Query("search") search: String?,
        @Query("id_user") iduser: Int
    ) : GetFavoriteProductListResponse

    @GET("api/ecommerce/get_detail_product")
    suspend fun getProductDetail(
        @Header("apikey") apikey: String,
        @Header("Authorization") token: String,
        @Query("id_product") idproduct: Int?,
        @Query("id_user") isuser: Int?
    ) : GetProductDetailResponse

    @FormUrlEncoded
    @POST("api/ecommerce/add_favorite")
    suspend fun addFavoriteProduct(
        @Header("apikey") apikey: String,
        @Header("Authorization") token: String,
        @Field("id_product") idproduct: Int?,
        @Field("id_user") iduser: Int?
    ) : AddFavoriteResponse

    @FormUrlEncoded
    @POST("api/ecommerce/remove_favorite")
    suspend fun removeFavoriteProduct(
        @Header("apikey") apikey: String,
        @Header("Authorization") token: String,
        @Field("id_product") idproduct: Int?,
        @Field("id_user") iduser: Int?
    ) : RemoveFavoriteResponse

    @POST("api/ecommerce/update-stock")
    suspend fun updateStock(
        @Header("apikey") apikey: String,
        @Header("Authorization") token: String,
        @Body dataStock: DataStock
    ) : UpdateStockResponse

    @FormUrlEncoded
    @PUT("api/ecommerce/update_rate/{id_product}")
    suspend fun updateRate(
        @Header("apikey") apikey: String,
        @Header("Authorization") token: String,
        @Path("id_product") idproduct: Int?,
        @Field("rate") rate: String
    ) : UpdateRateResponse

    @GET("api/ecommerce/get_list_product_paging")
    suspend fun getProductListPaging(
        @Query("search") search: String?,
        @Query("offset") offset: Int
    ) : GetProductListPagingResponse

    @GET("api/ecommerce/get_list_product_other")
    suspend fun getOtherProducts(
        @Query("id_user") iduser: Int?
    ) : GetOtherProductListResponse

    @GET("api/ecommerce/get_list_product_riwayat")
    suspend fun getProductSearchHistory(
        @Query("id_user") iduser: Int?
    ) : GetProductSearchHistoryResponse
}