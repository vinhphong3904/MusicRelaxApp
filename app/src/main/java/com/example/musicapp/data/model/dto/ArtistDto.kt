package com.example.musicapp.data.model.dto

data class ArtistDto(
    val id: Int,
    val name: String,
    val bio: String?,
    val image_url: String?,
    val is_verified: Boolean,
    val slug: String,
    val created_at: String
)

data class ArtistsResponse(
    val success: Boolean,
    val data: List<ArtistDto>
)

data class ArtistDetailResponse(
    val success: Boolean,
    val data: ArtistDto
)
