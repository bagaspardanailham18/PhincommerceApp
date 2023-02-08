package com.bagaspardanailham.myecommerceapp.ui.auth

import androidx.lifecycle.*
import com.bagaspardanailham.myecommerceapp.data.EcommerceRepository
import com.bagaspardanailham.myecommerceapp.data.local.preferences.PreferenceDataStore
import com.bagaspardanailham.myecommerceapp.data.local.preferences.UserPreferencesModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: EcommerceRepository, private val pref: PreferenceDataStore): ViewModel() {

    fun saveToken(authToken: String, refreshToken: String, id: Int?, name: String, email: String, phone: String, gender: String, imgPath: String) {
        viewModelScope.launch {
            pref.saveAuthToken(authToken, refreshToken, id, name, email, phone, gender, imgPath)
        }
    }

    fun getUserPref(): Flow<UserPreferencesModel?> = pref.userPreferencesFlow

    fun deleteToken() {
        viewModelScope.launch {
            pref.clearAuthToken()
        }
    }

    fun updateImgPath(path: String) {
        viewModelScope.launch {
            pref.updateImagePath(path)
        }
    }

    suspend fun loginUser(email: String, password: String, tokenFcm: String) = repository.loginUser(email, password, tokenFcm)

    suspend fun registerUser(
        email: RequestBody, password: RequestBody, name: RequestBody, gender: RequestBody, phone: RequestBody, image: MultipartBody.Part
    ) = repository.registerUser(name, email, password, phone, image, gender)

    suspend fun changePassword(
        auth: String, id: Int, pass: String, newPass: String, confirmNewPass: String
    ) = repository.changePassword(auth, id, pass, newPass, confirmNewPass)

}