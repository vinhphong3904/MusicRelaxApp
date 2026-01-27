package com.example.musicapp.data.service

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore("auth_prefs")
object TokenManager {

    private lateinit var appContext: Context

    private val TOKEN_KEY = stringPreferencesKey("jwt_token")

    // cache token cho interceptor
    @Volatile
    private var cachedToken: String? = null

    /** GỌI 1 LẦN DUY NHẤT */
    fun init(context: Context) {
        appContext = context.applicationContext

        CoroutineScope(Dispatchers.IO).launch {
            appContext.dataStore.data.collect { prefs ->
                cachedToken = prefs[TOKEN_KEY]
            }
        }
    }

    /** Dùng cho AuthInterceptor (SYNC) */
    fun getToken(): String? = cachedToken

    /** Gọi sau khi login */
    fun saveToken(token: String) {
        cachedToken = token
        CoroutineScope(Dispatchers.IO).launch {
            appContext.dataStore.edit { prefs ->
                prefs[TOKEN_KEY] = token
            }
        }
    }

    fun clearToken() {
        cachedToken = null
        CoroutineScope(Dispatchers.IO).launch {
            appContext.dataStore.edit { prefs ->
                prefs.remove(TOKEN_KEY)
            }
        }
    }
}

