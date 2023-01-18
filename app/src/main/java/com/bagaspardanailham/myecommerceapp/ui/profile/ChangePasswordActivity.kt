package com.bagaspardanailham.myecommerceapp.ui.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bagaspardanailham.myecommerceapp.data.Result
import com.bagaspardanailham.myecommerceapp.databinding.ActivityChangePasswordBinding
import com.bagaspardanailham.myecommerceapp.ui.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.bagaspardanailham.myecommerceapp.data.remote.response.ErrorResponse
import com.bagaspardanailham.myecommerceapp.ui.LoadingDialog
import com.bagaspardanailham.myecommerceapp.ui.MainActivity
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import org.json.JSONObject

@AndroidEntryPoint
class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding

    private val viewModel by viewModels<AuthViewModel>()

    private lateinit var loading: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loading = LoadingDialog(this)

        setCustomToolbar()
        setAction()
    }

    private fun setAction() {
        binding.apply {
            btnChangePassword.setOnClickListener {
                val oldPass = edtOldPassword.text.toString()
                val newPass = edtNewPassword.text.toString()
                val newPassConfirm = edtConfirmNewPassword.text.toString()

                var isAllChecked = false
                isAllChecked = formValidation(oldPass, newPass, newPassConfirm)

                if (isAllChecked) {
                    lifecycleScope.launch {
                        viewModel.getUserPref().collect { data ->
                            viewModel.changePassword(
                                data!!.authTokenKey,
                                data.id.toInt(),
                                oldPass,
                                newPass,
                                newPassConfirm
                            ).observe(this@ChangePasswordActivity) { response ->
                                when (response) {
                                    is Result.Loading -> {
                                        loading.startLoading()
                                    }
                                    is Result.Success -> {
                                        loading.isDismiss()
                                        Toast.makeText(this@ChangePasswordActivity, response.data.success?.message, Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(this@ChangePasswordActivity, MainActivity::class.java))
                                        finish()
                                    }
                                    is Result.Error -> {
                                        try {
                                            loading.isDismiss()
                                            val errorres = JSONObject(response.errorBody?.string()).toString()
                                            val gson = Gson()
                                            val jsonObject = gson.fromJson(errorres, JsonObject::class.java)
                                            val errorResponse = gson.fromJson(jsonObject, ErrorResponse::class.java)
                                            if (response.errorCode == 401) {
                                                Toast.makeText(this@ChangePasswordActivity, "Token is expired", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(this@ChangePasswordActivity, errorResponse.error?.message, Toast.LENGTH_SHORT).show()
                                            }
                                        } catch (e: Exception) {
                                            loading.isDismiss()
                                            Toast.makeText(this@ChangePasswordActivity, e.message, Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun formValidation(oldPass: String, newPass: String, newPassConfirm: String): Boolean {
        binding.apply {
            if (oldPass.isEmpty()) {
                layoutEdtOldPassword.error = "Old password is required!"
                return false
            }
            if (newPass.isEmpty()) {
                layoutEdtNewPassword.error = "New password is required!"
                return false
            } else if (newPass.length < 6) {
                layoutEdtNewPassword.error = "Password must be minimum 6 characters"
                return false
            }
            if (newPassConfirm.isEmpty()) {
                layoutEdtConfirmNewPassword.error = "Confirm new password is required!"
                return false
            }
            if (newPass != newPassConfirm) {
                layoutEdtNewPassword.error = "Password must be same"
                layoutEdtConfirmNewPassword.error = "Password must be same"
                return false
            }
            return true
        }
    }

    private fun setCustomToolbar() {
        setSupportActionBar(binding.customToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    companion object {
        private val USER_ID = "user_id"
    }
}