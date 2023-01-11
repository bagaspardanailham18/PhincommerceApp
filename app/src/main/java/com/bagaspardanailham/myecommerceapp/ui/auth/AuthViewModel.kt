package com.bagaspardanailham.myecommerceapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bagaspardanailham.myecommerceapp.data.EcommerceRepository
import com.bagaspardanailham.myecommerceapp.data.local.PreferenceDataStore
import com.bagaspardanailham.myecommerceapp.data.remote.ApiService
import com.bagaspardanailham.myecommerceapp.data.remote.response.LoginResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: EcommerceRepository, private val pref: PreferenceDataStore, private val apiService: ApiService): ViewModel() {

    fun saveToken(authToken: String, refreshToken: String) {
        viewModelScope.launch {
            pref.saveAuthToken(authToken, refreshToken)
        }
    }

    fun getAccessToken(): LiveData<String?> = pref.getAuthToken()

    fun deleteToken() {
        viewModelScope.launch {
            pref.clearAuthToken()
        }
    }

    suspend fun loginUser(email: String, password: String) = repository.loginUser(email, password)

    suspend fun registerUser(
        email: String, password: String, name: String, gender: Int, phone: String, image: String?
    ) = repository.registerUser(name, email, password, phone, image.toString(), gender)


}