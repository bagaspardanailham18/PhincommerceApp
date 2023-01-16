package com.bagaspardanailham.myecommerceapp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class PreferenceDataStore constructor(private val datastore: DataStore<Preferences>) {

    val userPreferencesFlow: Flow<UserPreferencesModel> = datastore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            mapUserPreferences(preferences)
        }

    private fun mapUserPreferences(pref: Preferences): UserPreferencesModel {
        val authToken = pref[AUTH_TOKEN_KEY].toString()
        val refreshToken = pref[REFRESH_TOKEN_KEY].toString()
        val id = pref[USER_ID].toString()
        val name = pref[USER_NAME].toString()
        val email = pref[USER_EMAIL].toString()
        val phone = pref[USER_PHONE].toString()
        val gender = pref[USER_GENDER].toString()
        val imgPath = pref[USER_IMG_PATH].toString()

        return UserPreferencesModel(authToken, refreshToken, id, name, email, phone, gender, imgPath)
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

    suspend fun saveSettingPref(langName: String) {
        datastore.edit { pref ->
            pref[LANG_SETTING] = langName
        }
    }

    val localSettingPrefFlow: Flow<LocalSettingPreferencesModel> = datastore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            mapLocalSettingPreferences(preferences)
        }

    private fun mapLocalSettingPreferences(pref: Preferences): LocalSettingPreferencesModel {
        val langName = pref[LANG_SETTING].toString()

        return LocalSettingPreferencesModel(langName)
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
        private val LANG_SETTING = stringPreferencesKey("lang_setting")
    }

}