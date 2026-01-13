package com.example.musicapp.data.model.dto

data class SongDto(
    val id: Int,
    val title: String,
    val duration: Int,
    val src: String,
    val cover_image_url: String,
    val view_count: Int,
    val slug: String,
    val artist_id: Int,
    val artist_name: String,
    val genre_id: Int,
    val genre_name: String
)

data class SongsResponse(
    val success: Boolean,
    val data: List<SongDto>,
    val pagination: PaginationDto
)

data class SongDetailResponse(
    val success: Boolean,
    val data: SongDto
)

data class PaginationDto(
    val page: Int,
    val limit: Int,
    val total: Int,
    val totalPages: Int
)
