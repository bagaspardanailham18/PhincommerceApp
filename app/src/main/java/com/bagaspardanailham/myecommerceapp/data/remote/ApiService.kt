package com.bagaspardanailham.myecommerceapp.data.remote

import com.bagaspardanailham.myecommerceapp.data.remote.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.Response
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("api/ecommerce/authentication")
    suspend fun loginUser(
        @Header("apikey") apikey: String,
        @Field("email") email: String,
        @Field("password") password: String
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

    @GET("api/ecommerce/get_detail_product?id_product=1")
    suspend fun getProductDetail(
        @Header("apikey") apikey: String,
        @Header("Authorization") token: String,
        @Query("id_product") idproduct: Int
    ) : GetProductDetailResponse
}