package com.bagaspardanailham.myecommerceapp.data.remote

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.bagaspardanailham.myecommerceapp.data.remote.response.auth.RefreshTokenResponse
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
            .baseUrl("https://portlan.id/training_android/public/")
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