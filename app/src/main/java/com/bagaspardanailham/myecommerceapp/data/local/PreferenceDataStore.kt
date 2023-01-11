package com.bagaspardanailham.myecommerceapp.data.local

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

    suspend fun saveAuthToken(authToken: String, refreshToken: String) {
        datastore.edit { preferences ->
            preferences[AUTH_TOKEN_KEY] = authToken
            preferences[REFRESH_TOKEN_KEY] = refreshToken
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
    }

}