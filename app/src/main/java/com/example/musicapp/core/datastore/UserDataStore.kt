package com.example.musicapp.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
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
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("user_prefs")
    private val ACCESS_TOKEN = stringPreferencesKey("access_token")

    suspend fun saveToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN] = token
        }
    }

    val tokenFLow: Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[ACCESS_TOKEN] }
}