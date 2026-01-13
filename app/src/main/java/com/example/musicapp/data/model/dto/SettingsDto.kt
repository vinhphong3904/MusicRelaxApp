package com.example.musicapp.data.model.dto

data class SettingsDto(
    val id: Int,
    val user_id: Int,
    val theme: String,
    val language: String,
    val notification_enabled: Boolean,
    val download_quality: String,
    val autoplay_next: Boolean,
    val explicit_filter: Boolean,
    val created_at: String,
    val updated_at: String?
)

data class SettingsResponse(
    val success: Boolean,
    val data: SettingsDto
)

data class UpdateSettingsRequest(
    val theme: String,
    val language: String,
    val notification_enabled: Boolean,
    val download_quality: String,
    val autoplay_next: Boolean,
    val explicit_filter: Boolean
)

data class UpdateSettingsResponse(
    val success: Boolean,
    val message: String
)
