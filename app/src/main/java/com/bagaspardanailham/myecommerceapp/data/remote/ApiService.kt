package com.bagaspardanailham.myecommerceapp.data.remote

import com.bagaspardanailham.myecommerceapp.data.remote.response.ChangePasswordResponse
import com.bagaspardanailham.myecommerceapp.data.remote.response.LoginResponse
import com.bagaspardanailham.myecommerceapp.data.remote.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

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
        @Field("id") id: Int,
        @Part image: MultipartBody.Part
    )
}