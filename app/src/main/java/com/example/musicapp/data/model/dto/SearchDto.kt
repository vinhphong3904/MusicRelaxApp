package com.example.musicapp.data.model.dto

data class SearchResponse(
    val success: Boolean,
    val data: SearchData
)

data class SearchData(
    val artists: List<ArtistDto>,
    val albums: List<AlbumDto>,
    val songs: List<SongDto>
)

data class ArtistDto(
    val id: Int,
    val name: String,
    val bio: String?,
    val image_url: String?,
    val is_verified: Boolean,
    val slug: String,
    val created_at: String
)
