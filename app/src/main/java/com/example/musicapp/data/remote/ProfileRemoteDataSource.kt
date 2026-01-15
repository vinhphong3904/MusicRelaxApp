package com.example.musicapp.data.remote

import com.example.musicapp.core.network.ApiService
import com.example.musicapp.data.model.dto.*
import okhttp3.MultipartBody
import javax.inject.Inject

class ProfileRemoteDataSource @Inject constructor(private val api: ApiService) {

    suspend fun fetchProfile(token: String): ProfileResponse {
        return api.getProfile("Bearer $token")
    }

    suspend fun updateProfile(token: String, request: UpdateProfileRequest): UpdateProfileResponse {
        return api.updateProfile("Bearer $token", request)
    }

    suspend fun uploadAvatar(token: String, avatarPart: MultipartBody.Part): AvatarResponse {
        return api.uploadAvatar("Bearer $token", avatarPart)
    }

    suspend fun resetAvatar(token: String): AvatarResponse {
        return api.resetAvatar("Bearer $token")
    }
}
