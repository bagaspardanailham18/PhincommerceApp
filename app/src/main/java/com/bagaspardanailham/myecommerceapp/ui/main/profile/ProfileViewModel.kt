package com.bagaspardanailham.myecommerceapp.ui.main.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bagaspardanailham.core.data.local.preferences.PreferenceDataStore
import com.bagaspardanailham.core.data.repository.EcommerceRepository
import com.bagaspardanailham.myecommerceapp.data.local.preferences.LocalSettingPreferencesModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val repository: EcommerceRepository, private val pref: PreferenceDataStore) : ViewModel() {

    suspend fun saveSettingPref(langName: String) {
        viewModelScope.launch {
            pref.saveSettingPref(langName)
        }
    }

    fun getSettingPref(): Flow<LocalSettingPreferencesModel?> = pref.localSettingPrefFlow

    suspend fun changeImage(id: Int, image: MultipartBody.Part) = repository.changeImage(id, image)

}