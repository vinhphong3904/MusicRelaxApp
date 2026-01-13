package com.example.musicapp.data.repository

import com.example.musicapp.data.remote.ProfileRemoteDataSource
import com.example.musicapp.data.model.dto.*
import okhttp3.MultipartBody

interface ProfileRepositoryInterface {
    suspend fun getProfile(token: String): ProfileResponse
    suspend fun updateProfile(token: String, request: UpdateProfileRequest): UpdateProfileResponse
    suspend fun uploadAvatar(token: String, avatarPart: MultipartBody.Part): AvatarResponse
    suspend fun resetAvatar(token: String): AvatarResponse
}

class ProfileRepository(
    private val remoteDataSource: ProfileRemoteDataSource
) : ProfileRepositoryInterface {
    override suspend fun getProfile(token: String): ProfileResponse {
        return remoteDataSource.fetchProfile(token)
    }

    override suspend fun updateProfile(token: String, request: UpdateProfileRequest): UpdateProfileResponse {
        return remoteDataSource.updateProfile(token, request)
    }

    override suspend fun uploadAvatar(token: String, avatarPart: MultipartBody.Part): AvatarResponse {
        return remoteDataSource.uploadAvatar(token, avatarPart)
    }

    override suspend fun resetAvatar(token: String): AvatarResponse {
        return remoteDataSource.resetAvatar(token)
    }
}
