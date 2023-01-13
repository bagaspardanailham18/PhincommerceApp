package com.bagaspardanailham.myecommerceapp.data.local

import android.os.Build.ID
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.map

class PreferenceDataStore constructor(private val datastore: DataStore<Preferences>) {

    fun getAuthToken(): LiveData<String?> {
        return datastore.data.map { preferences ->
            preferences[AUTH_TOKEN_KEY]
        }.asLiveData()
    }

    suspend fun saveAuthToken(authToken: String, refreshToken: String, id: Int?, name: String, email: String, phone: String, gender: String, imgPath: String) {
        datastore.edit { preferences ->
            preferences[AUTH_TOKEN_KEY] = authToken
            preferences[REFRESH_TOKEN_KEY] = refreshToken
            preferences[USER_ID] = id.toString()
            preferences[USER_NAME] = name
            preferences[USER_EMAIL] = email
            preferences[USER_PHONE] = phone
            preferences[USER_GENDER] = gender
            preferences[USER_IMG_PATH] = imgPath
        }
    }

    suspend fun clearAuthToken() {
        datastore.edit {
//            for removing spesific item
//            it.remove(TOKEN_KEY)
            it.clear()
        }
    }

    companion object {
        private val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token_data")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token_data")
        private val USER_ID = stringPreferencesKey("user_id")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val USER_PHONE = stringPreferencesKey("user_phone")
        private val USER_GENDER = stringPreferencesKey("user_gender")
        private val USER_IMG_PATH = stringPreferencesKey("user_img_path")
    }

}