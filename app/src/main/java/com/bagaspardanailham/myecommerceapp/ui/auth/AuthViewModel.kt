package com.bagaspardanailham.myecommerceapp.ui.auth

import androidx.lifecycle.*
import com.bagaspardanailham.core.data.repository.EcommerceRepository
import com.bagaspardanailham.core.data.repository.FirebaseAnalyticsRepository
import com.bagaspardanailham.core.data.local.preferences.PreferenceDataStore
import com.bagaspardanailham.core.data.local.preferences.UserPreferencesModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: EcommerceRepository, private val pref: PreferenceDataStore, private val firebaseAnalyticsRepository: FirebaseAnalyticsRepository): ViewModel() {

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
        email: RequestBody?, password: RequestBody?, name: RequestBody?, gender: Int, phone: RequestBody?, image: MultipartBody.Part?
    ) = repository.registerUser(name, email, password, phone, image, gender)

    suspend fun changePassword(
        id: Int, pass: String, newPass: String, confirmNewPass: String
    ) = repository.changePassword(id, pass, newPass, confirmNewPass)

    // Analytics
    fun onLoadSplash(screenClass: String) {
        firebaseAnalyticsRepository.onLoadSplash(screenClass)
    }

    fun onLoadLogin(screenClass: String) {
        firebaseAnalyticsRepository.onLoadLogin(screenClass)
    }

    fun onLoginButtonClicked(email: String) {
        firebaseAnalyticsRepository.onLoginButtonClicked(email)
    }

    fun onRegisterButtonClicked() {
        firebaseAnalyticsRepository.onClickButtonRegister()
    }

    fun onLoadSignup(screenClass: String) {
        firebaseAnalyticsRepository.onLoadRegister(screenClass)
    }

    fun onClickButtonLogin() {
        firebaseAnalyticsRepository.onClickButtonLogin()
    }

    fun onClickCameraIcon(screenName: String) {
        firebaseAnalyticsRepository.onClickCameraIcon(screenName)
    }

    fun onChangeImage(screenName: String, imageSource: String) {
        firebaseAnalyticsRepository.onChangeImage(screenName, imageSource)
    }

    fun onSignUpButtonClicked(imageSource: String?, email: String, name: String, phone: String, gender: String) {
        firebaseAnalyticsRepository.onSignupButtonClicked(imageSource, email, name, phone, gender)
    }
}