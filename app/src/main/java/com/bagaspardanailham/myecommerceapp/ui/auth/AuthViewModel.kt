package com.bagaspardanailham.myecommerceapp.ui.auth

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.*
import com.bagaspardanailham.myecommerceapp.data.EcommerceRepository
import com.bagaspardanailham.myecommerceapp.data.Result
import com.bagaspardanailham.myecommerceapp.data.local.PreferenceDataStore
import com.bagaspardanailham.myecommerceapp.data.local.PreferencesModel
import com.bagaspardanailham.myecommerceapp.data.remote.ApiService
import com.bagaspardanailham.myecommerceapp.data.remote.response.*
import com.bagaspardanailham.myecommerceapp.ui.MainActivity
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: EcommerceRepository, private val pref: PreferenceDataStore): ViewModel() {

    fun saveToken(authToken: String, refreshToken: String, id: Int?, name: String, email: String, phone: String, gender: String, imgPath: String) {
        viewModelScope.launch {
            pref.saveAuthToken(authToken, refreshToken, id, name, email, phone, gender, imgPath)
        }
    }

    fun getAccessToken(): Flow<PreferencesModel?> = pref.userPreferencesFlow

    fun deleteToken() {
        viewModelScope.launch {
            pref.clearAuthToken()
        }
    }

    suspend fun loginUser(email: String, password: String) = repository.loginUser(email, password)

    suspend fun registerUser(
        email: String, password: String, name: String, gender: Int, phone: String, image: String?
    ) = repository.registerUser(name, email, password, phone, image.toString(), gender)

    suspend fun changePassword(
        auth: String, id: Int, pass: String, newPass: String, confirmNewPass: String
    ) = repository.changePassword(auth, id, pass, newPass, confirmNewPass)

}