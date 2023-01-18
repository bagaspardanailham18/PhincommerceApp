package com.bagaspardanailham.myecommerceapp.data.remote

import android.content.Context
import android.content.Intent
import com.bagaspardanailham.myecommerceapp.ui.auth.AuthActivity
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class AuthErrorInterceptor @Inject constructor(private val tokenManager: TokenManager, private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val response = chain.proceed(request)
        when (response.code) {
            401 -> {
                runBlocking {
                    tokenManager.deleteToken()
                }
                val intent = Intent(context, AuthActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                return response
            }
        }
        return response
    }
}