package com.example.musicapp.domain.usecase

import com.example.musicapp.data.repository.SettingsRepositoryInterface
import com.example.musicapp.data.model.dto.SettingsDto

class GetSettingsUseCase(private val repo: SettingsRepositoryInterface) {
    suspend operator fun invoke(token: String) = repo.getSettings(token)
}

class UpdateSettingsUseCase(private val repo: SettingsRepositoryInterface) {
    suspend operator fun invoke(token: String, request: UpdateSettingsRequest) = repo.updateSettings(token, request)
}
