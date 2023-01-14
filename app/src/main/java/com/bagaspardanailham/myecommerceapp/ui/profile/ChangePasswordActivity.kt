package com.bagaspardanailham.myecommerceapp.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.accessibility.AccessibilityEventCompat.setAction
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import com.bagaspardanailham.myecommerceapp.R
import com.bagaspardanailham.myecommerceapp.databinding.ActivityChangePasswordBinding
import com.bagaspardanailham.myecommerceapp.ui.auth.AuthViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setCustomToolbar()
        setAction()
    }

    private fun setAction() {
        binding.apply {
            val oldPass = edtOldPassword.text.toString()
            val newPass = edtNewPassword.text.toString()
            val newPassConfirm = edtConfirmNewPassword.text.toString()

            btnChangePassword.setOnClickListener {
                when {
                    oldPass.isEmpty() -> {
                        layoutEdtOldPassword.error = "Old password is required!"
                        return@setOnClickListener
                    }
                    newPass.isEmpty() -> {
                        layoutEdtNewPassword.error = "New password is required!"
                        return@setOnClickListener
                    }
                    newPassConfirm.isEmpty() -> {
                        layoutEdtConfirmNewPassword.error = "Confirm new password is required!"
                        return@setOnClickListener
                    }
                    else -> {
                        if (newPass != newPassConfirm) {
                            layoutEdtNewPassword.error = "Password must be same"
                            layoutEdtConfirmNewPassword.error = "Password must be same"
                            return@setOnClickListener
                        } else {
                            lifecycleScope.launch {
                                viewModel.getAccessToken().observe(this@ChangePasswordActivity) { pref ->
                                    pref?.map {
                                        val userId = it.toString()
                                        Toast.makeText(this@ChangePasswordActivity, userId, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                }
            }
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