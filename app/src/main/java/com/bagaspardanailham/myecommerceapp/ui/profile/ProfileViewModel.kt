package com.bagaspardanailham.myecommerceapp.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bagaspardanailham.myecommerceapp.data.local.LocalSettingPreferencesModel
import com.bagaspardanailham.myecommerceapp.data.local.PreferenceDataStore
import com.bagaspardanailham.myecommerceapp.data.local.UserPreferencesModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val pref: PreferenceDataStore) : ViewModel() {

    fun saveSettingPref(langName: String) {
        viewModelScope.launch {
            pref.saveSettingPref(langName)
        }
    }

    fun getSettingPref(): Flow<LocalSettingPreferencesModel?> = pref.localSettingPrefFlow

}