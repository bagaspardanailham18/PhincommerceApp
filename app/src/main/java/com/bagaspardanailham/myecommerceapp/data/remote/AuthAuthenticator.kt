package com.bagaspardanailham.myecommerceapp.data.remote

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.bagaspardanailham.myecommerceapp.data.EcommerceRepository
import com.bagaspardanailham.myecommerceapp.data.local.PreferenceDataStore
import com.bagaspardanailham.myecommerceapp.data.remote.response.LoginResponse
import com.bumptech.glide.load.engine.Resource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import com.bagaspardanailham.myecommerceapp.data.Result
import com.bagaspardanailham.myecommerceapp.data.TokenResult
import com.bagaspardanailham.myecommerceapp.data.remote.response.RefreshTokenResponse
import javax.inject.Inject

class AuthAuthenticator @Inject constructor(
    private val tokenManager: TokenManager
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking {
            val authToken = tokenManager.getAuthToken().first().toString()
            val refreshToken = tokenManager.getRefreshToken().first().toString()
            val userId = tokenManager.getidUser().first().toString().toInt()

            val newToken = getNewToken(userId, authToken, refreshToken)

            if (!newToken.isSuccessful || newToken.body() == null) {
                tokenManager.deleteToken()
            }

            newToken.body()?.let {
                tokenManager.saveAccessToken(it.success?.accessToken.toString())
                tokenManager.saveRefreshToken(it.success?.refreshToken.toString())
                response.request.newBuilder()
                    .header("Authorization", it.success?.accessToken.toString())
                    .build()
            }
        }
    }

    private suspend fun getNewToken(id: Int, accessToken: String, refreshToken: String): retrofit2.Response<RefreshTokenResponse> {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://172.17.20.201/training_android/public/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        val service = retrofit.create(ApiService::class.java)
        return service.refreshToken(API_KEY, id, accessToken, refreshToken)
    }

    companion object {
        const val API_KEY = "TuIBt77u7tZHi8n7WqUC"
    }
}