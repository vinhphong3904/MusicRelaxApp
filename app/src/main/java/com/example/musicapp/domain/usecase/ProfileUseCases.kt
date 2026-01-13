package com.example.musicapp.domain.usecase

import com.example.musicapp.data.repository.ProfileRepositoryInterface
import com.example.musicapp.data.model.dto.*
import okhttp3.MultipartBody

class GetProfileUseCase(private val repository: ProfileRepositoryInterface) {
    suspend operator fun invoke(token: String): ProfileResponse {
        return repository.getProfile(token)
    }
}

class UpdateProfileUseCase(private val repository: ProfileRepositoryInterface) {
    suspend operator fun invoke(token: String, request: UpdateProfileRequest): UpdateProfileResponse {
        return repository.updateProfile(token, request)
    }
}

class UploadAvatarUseCase(private val repository: ProfileRepositoryInterface) {
    suspend operator fun invoke(token: String, avatarPart: MultipartBody.Part): AvatarResponse {
        return repository.uploadAvatar(token, avatarPart)
    }
}

class ResetAvatarUseCase(private val repository: ProfileRepositoryInterface) {
    suspend operator fun invoke(token: String): AvatarResponse {
        return repository.resetAvatar(token)
    }
}
