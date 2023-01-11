package com.bagaspardanailham.myecommerceapp.data.remote

import com.bagaspardanailham.myecommerceapp.data.remote.response.LoginResponse
import com.bagaspardanailham.myecommerceapp.data.remote.response.RegisterResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {

    @FormUrlEncoded
    @POST("api/ecommerce/authentication")
    suspend fun loginUser(
        @Header("apikey") apikey: String,
        @Field("email") email: String,
        @Field("password") password: String
    ) : LoginResponse

    @FormUrlEncoded
    @POST("api/ecommerce/registration")
    suspend fun registerUser(
        @Header("apikey") apikey: String,
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("phone") phone: String,
        @Field("gender") gender: Int,
        @Field("image") image: String,
    ) : RegisterResponse

}