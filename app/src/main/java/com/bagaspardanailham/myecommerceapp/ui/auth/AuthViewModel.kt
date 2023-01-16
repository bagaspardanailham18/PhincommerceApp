package com.bagaspardanailham.myecommerceapp.ui.auth

import androidx.lifecycle.*
import com.bagaspardanailham.myecommerceapp.data.EcommerceRepository
import com.bagaspardanailham.myecommerceapp.data.local.PreferenceDataStore
import com.bagaspardanailham.myecommerceapp.data.local.PreferencesModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: EcommerceRepository, private val pref: PreferenceDataStore): ViewModel() {

    fun saveToken(authToken: String, refreshToken: String, id: Int?, name: String, email: String, phone: String, gender: String, imgPath: String) {
        viewModelScope.launch {
            pref.saveAuthToken(authToken, refreshToken, id, name, email, phone, gender, imgPath)
        }
    }

    fun getUserPref(): Flow<PreferencesModel?> = pref.userPreferencesFlow

    fun deleteToken() {
        viewModelScope.launch {
            pref.clearAuthToken()
        }
    }

    suspend fun loginUser(email: String, password: String) = repository.loginUser(email, password)

    suspend fun registerUser(
        email: String, password: String, name: String, gender: Int, phone: String, image: MultipartBody.Part
    ) = repository.registerUser(name, email, password, phone, image, gender)

    suspend fun changePassword(
        auth: String, id: Int, pass: String, newPass: String, confirmNewPass: String
    ) = repository.changePassword(auth, id, pass, newPass, confirmNewPass)

}