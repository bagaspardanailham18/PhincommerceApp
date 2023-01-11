package com.bagaspardanailham.myecommerceapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bagaspardanailham.myecommerceapp.R
import com.bagaspardanailham.myecommerceapp.ui.auth.AuthActivity
import com.bagaspardanailham.myecommerceapp.ui.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.text.Typography.dagger

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private val viewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        determiningDirection()
    }

    private fun determiningDirection() {
        lifecycleScope.launchWhenCreated {
            delay(3000)
            viewModel.getAccessToken().observe(this@SplashScreenActivity) { data ->
                if (data.isNullOrEmpty() || data == "") {
                    Log.d("token", "Token : $data")
                    startActivity(Intent(this@SplashScreenActivity, AuthActivity::class.java))
                    finish()
                } else {
                    Log.d("token", "Token : $data")
                    startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                    finish()
                }
            }
        }
    }
}