package com.bagaspardanailham.myecommerceapp.ui.main.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat.startActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.bagaspardanailham.myecommerceapp.R
import com.bagaspardanailham.core.data.Result
import com.bagaspardanailham.core.data.repository.FirebaseAnalyticsRepository
import com.bagaspardanailham.core.utils.Constant
import com.bagaspardanailham.myecommerceapp.databinding.ActivityChangePasswordBinding
import com.bagaspardanailham.myecommerceapp.ui.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.bagaspardanailham.core.data.remote.response.ErrorResponse
import com.bagaspardanailham.myecommerceapp.ui.LoadingDialog
import com.bagaspardanailham.myecommerceapp.ui.main.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class ChangePasswordActivity : AppCompatActivity() {

    @Inject
    lateinit var firebaseAnalyticsRepository: FirebaseAnalyticsRepository

    private lateinit var binding: ActivityChangePasswordBinding

    private val viewModel by viewModels<AuthViewModel>()

    private lateinit var loading: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loading = LoadingDialog(this)

        setCustomToolbar()

        formValidation()
        setAction()
    }

    private fun setAction() {
            binding.btnChangePassword.setOnClickListener {
                handleChangePassword()
                // Analytics
                firebaseAnalyticsRepository.onClickButtonSave()
            }
    }

    private fun handleChangePassword() {
        binding.apply {
            val oldPass = binding.edtOldPassword.text.toString().trim()
            val newPass = binding.edtNewPassword.text.toString().trim()
            val newPassConfirm = binding.edtConfirmNewPassword.text.toString().trim()

            if (oldPass.isEmpty()) {
                layoutEdtOldPassword.error = getString(R.string.old_password_is_required)
            } else if (newPass.isEmpty()) {
                layoutEdtNewPassword.error = getString(R.string.new_password_is_required)
            } else if (newPass.length < 6) {
                layoutEdtNewPassword.error = getString(R.string.password_must_be_minimum_6_char)
            } else if (newPassConfirm.isEmpty()) {
                layoutEdtConfirmNewPassword.error = getString(R.string.confirm_new_password_is_required)
            } else if (newPassConfirm != newPass) {
                layoutEdtConfirmNewPassword.error = getString(R.string.password_must_be_same)
            } else {
                lifecycleScope.launch {
                    viewModel.getUserPref().collect { data ->
                        viewModel.changePassword(
                            data?.id!!.toInt(),
                            oldPass,
                            newPass,
                            newPassConfirm
                        ).collect { response ->
                            when (response) {
                                is Result.Loading -> {
                                    loading.startLoading()
                                }
                                is Result.Success -> {
                                    loading.isDismiss()
                                    showSuccessDialog()
                                    Toast.makeText(this@ChangePasswordActivity, response.data.success?.message, Toast.LENGTH_SHORT).show()
                                }
                                is Result.Error -> {
                                    try {
                                        loading.isDismiss()
                                        val errorres = JSONObject(response.errorBody?.string()).toString()
                                        val gson = Gson()
                                        val jsonObject = gson.fromJson(errorres, JsonObject::class.java)
                                        val errorResponse = gson.fromJson(jsonObject, ErrorResponse::class.java)
                                        if (response.errorCode == 401) {
                                            Toast.makeText(this@ChangePasswordActivity, getString(R.string.token_is_expired), Toast.LENGTH_SHORT).show()
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

    private fun formValidation() {
        binding.apply {
            edtOldPassword.doOnTextChanged { text, start, before, count ->
                if (text.toString().isEmpty()) {
                    layoutEdtOldPassword.error = getString(R.string.old_password_is_required)
                } else {
                    layoutEdtOldPassword.error = null
                    layoutEdtOldPassword.isErrorEnabled = false
                }
            }
            edtNewPassword.doOnTextChanged { text, start, before, count ->
                if (text.toString().isEmpty()) {
                    layoutEdtNewPassword.error = getString(R.string.new_password_is_required)
                } else if (text.toString().length < 6) {
                    layoutEdtNewPassword.error = getString(R.string.password_must_be_minimum_6_char)
                } else {
                    layoutEdtNewPassword.error = null
                    layoutEdtNewPassword.isErrorEnabled = false
                }
            }
            edtConfirmNewPassword.doOnTextChanged { text, start, before, count ->
                if (text.toString().isEmpty()) {
                    layoutEdtConfirmNewPassword.error = getString(R.string.confirm_new_password_is_required)
                } else if (text.toString() != edtNewPassword.text.toString()) {
                    layoutEdtConfirmNewPassword.error = getString(R.string.password_must_be_same)
                } else {
                    layoutEdtConfirmNewPassword.error = null
                    layoutEdtConfirmNewPassword.isErrorEnabled = false
                }
            }
        }
    }

    private fun showSuccessDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(String.format(resources.getString(R.string.success), resources.getString(R.string.string_change_password)))
            .setMessage(String.format(resources.getString(R.string.successfully), resources.getString(
                R.string.string_change_password)))
            .setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->
                startActivity(Intent(this@ChangePasswordActivity, MainActivity::class.java))
                finish()
            }
            .show()
    }

    private fun setCustomToolbar() {
        setSupportActionBar(binding.customToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        firebaseAnalyticsRepository.onClickBackIcon(
            Constant.CHANGE_PASSWORD
        )
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()

        // Analytics
        firebaseAnalyticsRepository.onLoadScreen(
            Constant.CHANGE_PASSWORD, this.javaClass.simpleName
        )
    }
}











