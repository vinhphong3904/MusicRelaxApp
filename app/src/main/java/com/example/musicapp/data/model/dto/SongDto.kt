package com.example.musicapp.data.model.dto

import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.model.SongDetail

data class SongDto(
    val id: Int,
    val title: String,
    val duration: Int,
    val src: String,
    val cover_image_url: String?,
    val view_count: Int,
    val slug: String?,
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

data class SongDetailDto(
    val id: Int,
    val title: String,
    val src: String,
    val duration: Int,
    val cover_image_url: String?,
    val lyrics_content: String?,
    val view_count: Int,
    val slug: String?,
    val artist_id: Int,
    val artist_name: String,
    val artist_image: String?,
    val genre_id: Int,
    val genre_name: String,
    val album_id: Int?,
    val album_title: String?,
    val album_cover: String?
)

data class SongDetailResponse(
    val success: Boolean,
    val data: SongDetailDto
)

data class PaginationDto(
    val page: Int,
    val limit: Int,
    val total: Int,
    val totalPages: Int
)

data class SongTopDto(
    val id: Int,
    val title: String,
    val artist_id: Int,
    val album_id: Int?,
    val genre_id: Int?,
    val duration_seconds: Int,
    val audio_url: String,
    val cover_image_url: String?,
    val view_count: Long,
    val slug: String?,
    val created_at: String?
)

data class SongsSimpleResponse(
    val success: Boolean,
    val data: List<SongTopDto>
)
