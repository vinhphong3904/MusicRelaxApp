package com.example.musicapp.data.model.dto

data class ProfileDto(
    val id: Int? = null,
    val user_id: Int,
    val full_name: String,
    val date_of_birth: String,
    val gender: String,
    val phone: String,
    val address: String,
    val bio: String,
    val songs_played_count: Int? = null,
    val songs_downloaded_count: Int? = null,
    val artists_followed_count: Int? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
    val username: String? = null,
    val email: String? = null,
    val avatar_url: String? = null,
    val role: String? = null
)

data class ProfileResponse(
    val success: Boolean,
    val data: ProfileDto
)

data class UpdateProfileRequest(
    val full_name: String,
    val date_of_birth: String,
    val gender: String,
    val phone: String,
    val address: String,
    val bio: String
)

data class UpdateProfileResponse(
    val success: Boolean,
    val message: String
)

data class AvatarResponse(
    val success: Boolean,
    val message: String,
    val avatar_url: String
)
