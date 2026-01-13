package com.example.musicapp.data.remote

import com.example.musicapp.core.network.ApiService
import com.example.musicapp.data.model.dto.*

class SettingsRemoteDataSource(private val api: ApiService) {
    suspend fun fetchSettings(token: String) = api.getSettings("Bearer $token")
    suspend fun updateSettings(token: String, request: UpdateSettingsRequest) = api.updateSettings("Bearer $token", request)
}
