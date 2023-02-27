package com.bagaspardanailham.core.data.remote

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(private val datastore: DataStore<Preferences>) {
    companion object {
        private val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token_data")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token_data")
        private val USER_ID = stringPreferencesKey("user_id")
    }

    fun getAuthToken(): Flow<String?> {
        return datastore.data.map { preferences ->
            preferences[AUTH_TOKEN_KEY]
        }
    }

    fun getRefreshToken(): Flow<String?> {
        return datastore.data.map { preferences ->
            preferences[REFRESH_TOKEN_KEY]
        }
    }

    fun getidUser(): Flow<String?> {
        return datastore.data.map { preferences ->
            preferences[USER_ID]
        }
    }

    suspend fun saveAccessToken(token: String) {
        datastore.edit { preferences ->
            preferences[AUTH_TOKEN_KEY] = token
        }
    }

    suspend fun saveRefreshToken(token: String) {
        datastore.edit { preferences ->
            preferences[REFRESH_TOKEN_KEY] = token
        }
    }

    suspend fun deleteToken() {
        datastore.edit { preferences ->
            preferences.clear()
        }
    }
}