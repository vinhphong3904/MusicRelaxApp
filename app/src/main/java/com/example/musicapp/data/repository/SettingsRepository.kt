package com.example.musicapp.data.repository
 
import com.example.musicapp.data.model.dto.SettingsDto
import com.example.musicapp.data.model.dto.UpdateSettingsRequest
import com.example.musicapp.data.remote.SettingsRemoteDataSource
import javax.inject.Inject

interface SettingsRepositoryInterface {
    suspend fun getSettings(token: String): SettingsDto
    suspend fun updateSettings(token: String, request: UpdateSettingsRequest): String
}

class SettingsRepository @Inject constructor(private val remoteDataSource: SettingsRemoteDataSource) : SettingsRepositoryInterface {
    override suspend fun getSettings(token: String) = remoteDataSource.fetchSettings(token).data
    override suspend fun updateSettings(token: String, request: UpdateSettingsRequest) = remoteDataSource.updateSettings(token, request).message
}
