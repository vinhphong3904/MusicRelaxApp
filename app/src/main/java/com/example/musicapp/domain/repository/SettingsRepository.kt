package com.example.musicapp.domain.repository
 
import com.example.musicapp.data.model.dto.SettingsDto
import com.example.musicapp.data.remote.SettingsRemoteDataSource

interface SettingsRepositoryInterface {
    suspend fun getSettings(token: String): SettingsDto
    suspend fun updateSettings(token: String, request: UpdateSettingsRequest): String
}

class SettingsRepository(private val remote: SettingsRemoteDataSource) : SettingsRepositoryInterface {
    override suspend fun getSettings(token: String) = remote.fetchSettings(token).data
    override suspend fun updateSettings(token: String, request: UpdateSettingsRequest) = remote.updateSettings(token, request).message
}
