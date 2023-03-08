package com.bagaspardanailham.core.data.remote

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(private val tokenManager: TokenManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val token = runBlocking {
            withContext(Dispatchers.IO) {
                tokenManager.getAuthToken().first()
            }
        }

        val requestBuilder = originalRequest.newBuilder()
            .header("apikey", API_KEY)
            .header("Authorization", token.toString())

        val request = requestBuilder.build()

        return chain.proceed(request)
    }

    companion object {
        const val API_KEY = "TuIBt77u7tZHi8n7WqUC"
    }
}