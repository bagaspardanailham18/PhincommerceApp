package com.bagaspardanailham.core.data.remote

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.bagaspardanailham.core.data.remote.TokenManager
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
                val intent = Intent(context, Class.forName("com.bagaspardanailham.myecommerceapp.ui.auth.AuthActivity"))
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                return response
            }

            429 -> {
                Log.d("error", "Too many request!!")
                return response
            }
        }
        return response
    }
}