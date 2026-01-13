package com.example.musicapp.data.model.dto

data class AlbumDto(
    val id: Int,
    val title: String,
    val release_date: String,
    val description: String,
    val cover_image_url: String,
    val created_at: String,
    val artist_id: Int,
    val artist_name: String
)

data class AlbumsResponse(
    val success: Boolean,
    val data: List<AlbumDto>,
    val pagination: PaginationDto
)

data class AlbumDetailResponse(
    val success: Boolean,
    val data: AlbumDto
)
