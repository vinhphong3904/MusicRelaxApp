package com.example.musicapp.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Setup DataStore cơ bản (đây là code rút gọn, thực tế cần setup keys)
    private val Context.dataStore by preferencesDataStore("user_prefs")
    private val TOKEN_KEY = stringPreferencesKey("jwt_token")

    suspend fun saveToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
    }

    val accessToken: Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[TOKEN_KEY] }
}