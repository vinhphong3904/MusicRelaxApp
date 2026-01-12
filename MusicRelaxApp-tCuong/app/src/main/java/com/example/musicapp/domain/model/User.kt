package com.example.musicapp.domain.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val role: String,
    @SerializedName("avatar_url") val avatarUrl: String?,
    @SerializedName("full_name") val fullName: String?, // Từ bảng profiles

    // Settings (có thể null nếu chưa setup)
    val settings: UserSettingsDto?
)

data class UserSettingsDto(
    val theme: String,
    val language: String,
    @SerializedName("download_quality") val downloadQuality: String
)